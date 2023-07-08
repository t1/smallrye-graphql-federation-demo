package org.example.orderPrinter;

import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import io.smallrye.graphql.client.typesafe.api.GraphQLClientApi;
import org.eclipse.microprofile.graphql.Query;

import jakarta.inject.Inject;
import java.util.stream.Stream;

@QuarkusMain
public class PrintCommand implements QuarkusApplication {
    @GraphQLClientApi(configKey = "order-api")
    public interface Orders {
        @Query
        Order order(String id);
    }

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
