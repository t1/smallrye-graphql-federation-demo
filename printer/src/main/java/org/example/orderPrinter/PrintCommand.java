package org.example.orderPrinter;

import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import java.util.stream.Stream;

@QuarkusMain
public class PrintCommand implements QuarkusApplication {
    @Path("/orders")
    @RegisterRestClient(configKey = "order-api")
    public interface OrderApi {
        @GET @Path("/{id}")
        Order order(@PathParam("id") String id);
    }

    @Inject @RestClient OrderApi orders;

    @Override
    public int run(String... orderIds) {
        Stream.of(orderIds)
            .map(orders::order)
            .map(Printer::new)
            .forEach(Printer::print);
        return 0;
    }
}
