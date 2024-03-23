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
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.BDDAssertions.then;

@QuarkusTest
class PrinterIT {
    final static PrintStream realOut = System.out;
    final static ByteArrayOutputStream out = new ByteArrayOutputStream();

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
        System.setOut(new PrintStream(out));
        System.setErr(new PrintStream(out));
        startContainers();
        startFederationGateway();
    }

    private static void startContainers() {
        Startables.deepStart(PRODUCT, ORDER, PRICE).join();
    }

    @SneakyThrows(IOException.class)
    private static void startFederationGateway() {
        gateway = new ProcessBuilder("./router")
                .directory(Path.of("../gateway").toFile())
                .redirectOutput(gatewayOut.toFile())
                .redirectError(gatewayErr.toFile())
                .start();
        var ready = waitFor(() -> Files.readString(gatewayOut).contains("Server listening"));
        if (!ready) throw new IllegalStateException("gateway not ready");
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
        if (gateway.isAlive()) gateway.destroy();
        waitFor(() -> !gateway.isAlive());
        realOut.println("gateway out:");
        realOut.println(Files.readString(gatewayOut));
        realOut.println("gateway err:");
        realOut.println(Files.readString(gatewayErr));
        realOut.println("gateway exit value: " + gateway.exitValue());
        writers.forEach(PrinterIT::close);
    }

    @SneakyThrows(IOException.class) private static void close(BufferedWriter writer) {writer.close();}

    @BeforeEach void beforeEach() {out.reset();}


    @Inject PrintCommand printCommand;

    @Test void shouldPrintOrder() {
        printCommand.run("1");

        realOut.println(out);
        then(out.toString()).contains(
                "Order: 21/8820.1756\n" +
                "Date : 2021-06-09\n" +
                "Name : Jane Doe   [123456]\n" +
                "\n" +
                " #  n  Product    Price   Total\n" +
                " 1  1    Table   399.99   399.99\n" +
                " 2  4    Chair   199.99   799.96\n" +
                "\n" +
                "Total:  5 Packages       1199.95\n" +
                "\n" +
                "\n");
    }
}
