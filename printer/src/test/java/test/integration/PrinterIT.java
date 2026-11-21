package test.integration;

import com.github.t1.testcontainers.jee.JeeContainer;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import lombok.SneakyThrows;
import org.example.orderPrinter.PrintCommand;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.output.OutputFrame;
import org.testcontainers.lifecycle.Startables;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeoutException;

import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;
import static org.assertj.core.api.BDDAssertions.then;

@QuarkusTest
class PrinterIT {
    final static PrintStream realOut = System.out;
    final static ByteArrayOutputStream out = new ByteArrayOutputStream();
    static BufferedWriter debugLog;

    static {
        try {
            Files.createDirectories(Paths.get("target"));
            debugLog = Files.newBufferedWriter(Paths.get("target/debug-log.txt"), CREATE, APPEND);
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
    }

    @SneakyThrows
    private static void dbg(String msg) {if (debugLog != null) {debugLog.write(msg + "\n"); debugLog.flush();}}

    static List<BufferedWriter> writers = new ArrayList<>();
    // we don't use the @Container annotation, so we can start the containers in parallel
    static JeeContainer PRODUCT = container("product", 18482);
    static JeeContainer ORDER = container("order", 18483);
    static JeeContainer PRICE = container("price", 18484);

    static Process gateway;
    static Path gatewayOut = log("gateway-out");
    static Path gatewayErr = log("gateway-err");

    @NotNull private static Path log(String name) {return Paths.get("target/" + name + ".log");}

    @SneakyThrows(IOException.class) @SuppressWarnings("resource")
    private static JeeContainer container(String deployable, int hostPort) {
        var log = Files.newBufferedWriter(log(deployable));
        writers.add(log);

        return JeeContainer.create("rdohna/wildfly:31.0-jdk17-graphql")
                .withDeployment("../" + deployable + "/target/ROOT.war")
                .withMainPortBoundToFixedPort(hostPort)
                .withLogConsumer(frame -> append(frame, log));
    }

    @SneakyThrows(IOException.class)
    private static void append(OutputFrame frame, BufferedWriter log) {
        log.append(frame.getUtf8String());
    }

    @BeforeAll
    static void beforeAll() {
        Runtime.getRuntime().addShutdownHook(new Thread(PrinterIT::stopGateway, "test-gateway-shutdown"));
        System.setOut(new PrintStream(out));
        System.setErr(new PrintStream(out));
        startContainers();
        startFederationGateway();
    }

    /** Kill all gateway processes by matching their command-line patterns. */
    private static void stopGateway() {
        dbg("stopGateway invoked, gateway=" + (gateway == null ? "null" : gateway.getClass().getName()));
        dbg("  isAlive=" + (gateway != null && gateway.isAlive()));
        dbg("  GRAPHQL_GATEWAY=" + System.getenv("GRAPHQL_GATEWAY"));

        if (gateway != null && !gateway.isAlive()) {
            dbg("stopGateway early-return (not alive)");
            return;
        }

        long pid = gateway != null ? gateway.pid() : -1;
        dbg("  gateway.pid()=" + pid);

        String gwType = System.getenv("GRAPHQL_GATEWAY");
        try {
            if ("the-guild-conductor".equals(gwType)) {
                dbg("killing conductor pattern...");
                killByName("conductor.*config\\.yaml");
            } else {
                dbg("killing router pattern...");
                killByName("router@\\d+\\.\\d+-");
            }
        } catch (Exception e) {
            dbg("killByName failed: " + e);
            e.printStackTrace(System.err);
        }

        // Also destroy the tracked handle
        if (gateway != null) {
            dbg("calling gateway.destroyForcibly()");
            gateway.destroyForcibly();
            try {Thread.sleep(200);} catch (InterruptedException ie) {Thread.currentThread().interrupt();}
            if (gateway.isAlive()) {
                dbg("still alive after destroyForcibly, trying killByPidDirect... pid=" + gateway.pid());
                killByPidDirect(gateway.pid());
            } else {
                dbg("destroyForcibly succeeded");
            }
        }

        dbg("stopGateway done");
    }

    /** Use pkill to find and terminate any process whose full cmdline matches the regex. */
    private static void killByName(String pattern) throws IOException, InterruptedException {
        dbg("  >>> calling pkill with pattern='" + pattern + "'");
        var pb = new ProcessBuilder("bash", "-c",
                "pgrep -af '" + pattern + "' 2>/dev/null || echo 'no matches found'; pkill -9 -f '" + pattern + "'");
        var proc = pb.start();
        byte[] outBuf = new byte[4096];
        InputStream is = proc.getInputStream();
        int n = is.read(outBuf);
        is.close();
        proc.waitFor(5, java.util.concurrent.TimeUnit.SECONDS);
        dbg("  <<< pkill rc=" + proc.exitValue() + " output=[" + new String(outBuf, 0, Math.min(n, outBuf.length)).stripTrailing() + "]");
    }

    /** Kill a specific PID directly via bash kill command. */
    private static void killByPidDirect(long pid) {
        try {
            new ProcessBuilder("bash", "-c", "pkill -9 -P " + pid + "; kill -9 " + pid)
                    .start().waitFor(3, java.util.concurrent.TimeUnit.SECONDS);
        } catch (InterruptedException e) {Thread.currentThread().interrupt();} catch (IOException ignored) {}
    }

    private static void startContainers() {
        Startables.deepStart(PRODUCT, ORDER, PRICE).join();
    }

    @SneakyThrows(IOException.class)
    private static void startFederationGateway() {
        gateway = new ProcessBuilder("./gateway")
                .directory(Path.of("../gateway").toFile())
                .redirectOutput(gatewayOut.toFile())
                .redirectError(gatewayErr.toFile())
                .start();
        var ready = waitFor(() -> isServerListening(Files.readString(gatewayOut)));
        if (!ready) {
            if (!gateway.isAlive()) {
                var msg = "gateway crashed (exit code %d):\nGRAPHQL_GATEWAY=%s\nstdout:\n%s\nstderr:\n%s"
                        .formatted(gateway.exitValue(), System.getenv("GRAPHQL_GATEWAY"),
                                Files.readString(gatewayOut).stripTrailing(),
                                Files.readString(gatewayErr).stripTrailing());
                throw new IllegalStateException(msg);
            }
            throw new IllegalStateException("gateway not ready within 15s");
        }
    }

    private static boolean isServerListening(String stdout) {
        return stdout.contains("Server listening") // wundergraph
               || stdout.contains("Actix runtime found; starting in Actix runtime"); // the-guild conductor
    }

    @SneakyThrows @SuppressWarnings("BusyWait")
    private static boolean waitFor(Callable<Boolean> condition) {
        var timeout = Instant.now().plusSeconds(15);
        while (Instant.now().isBefore(timeout)) {
            if (condition.call()) return true;
            Thread.sleep(100);
            if (!gateway.isAlive()) return false;
        }
        throw new TimeoutException();
    }

    @AfterAll
    static void afterAll() throws IOException {
        dbg("@AfterAll starting");
        stopGateway();
        realOut.println("[PrinterIT] printing gateway logs...");
        realOut.println("gateway out:");
        realOut.println(Files.readString(gatewayOut));
        realOut.println("gateway err:");
        realOut.println(Files.readString(gatewayErr));
        if (gateway != null && !gateway.isAlive()) {
            realOut.println("gateway exit value: " + gateway.exitValue());
        }
        writers.forEach(PrinterIT::close);
    }

    @SneakyThrows(IOException.class) private static void close(BufferedWriter writer) {writer.close();}

    @BeforeEach void beforeEach() {out.reset();}


    @Inject PrintCommand printCommand;

    @Test void shouldPrintOrder() {
        printCommand.run("1");

        realOut.println(out);
        then(out.toString()).contains("""
                Order: 21/8820.1756
                Date : 2021-06-09
                Name : Jane Doe   [123456]
                
                 #  n  Product    Price   Total
                 1  1    Table   399.99   399.99
                 2  4    Chair   199.99   799.96
                
                Total:  5 Packages       1199.95
                
                
                """);
    }
}
