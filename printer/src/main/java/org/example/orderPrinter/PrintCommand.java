package org.example.orderPrinter;

import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;

import javax.inject.Inject;
import java.util.stream.Stream;

@QuarkusMain
public class PrintCommand implements QuarkusApplication {
    @Inject Orders orders;

    @Override
    public int run(String... orderIds) {
        Stream.of(orderIds)
            .map(orders::order)
            .map(Printer::new)
            .forEach(Printer::print);
        return 0;
    }
}
