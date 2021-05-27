package org.example.bff;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("/orders")
public class Orders {
    @Path("/orders")
    @RegisterRestClient(configKey = "order-api")
    public interface OrderApi {
        @GET @Path("/{id}")
        Order order(@PathParam("id") String id);
    }

    @Path("/products")
    @RegisterRestClient(configKey = "product-api")
    public interface ProductApi {
        @GET @Path("/{id}")
        Product product(@PathParam("id") String id);
    }

    @Inject @RestClient OrderApi orders;
    @Inject @RestClient ProductApi products;

    @GET @Path("/{id}")
    public Order order(@PathParam("id") String id) {
        var order = orders.order(id);
        for (var orderItem : order.getOrderItems())
            orderItem.setProduct(products.product(orderItem.getProductId()));
        return order;
    }
}
