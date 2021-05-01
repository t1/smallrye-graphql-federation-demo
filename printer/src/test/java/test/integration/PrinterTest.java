package test.integration;

import io.quarkus.test.junit.QuarkusTest;
import org.example.orderPrinter.PrintCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.assertj.core.api.BDDAssertions.then;

@QuarkusTest
class PrinterTest {
    final PrintStream sout = System.out;
    final ByteArrayOutputStream out = new ByteArrayOutputStream();

    @BeforeEach void beforeEach() {
        System.setOut(new PrintStream(out));
    }

    @Inject PrintCommand printCommand;

    @Test void shouldPrintOrder() {
        printCommand.run("1");

        sout.println(out);
        then(out).hasToString("\n" +
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
