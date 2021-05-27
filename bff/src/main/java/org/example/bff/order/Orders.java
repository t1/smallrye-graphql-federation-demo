package org.example.bff.order;

import io.smallrye.graphql.client.typesafe.api.GraphQLClientApi;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Query;

import javax.inject.Inject;

@GraphQLApi
public class Orders {
    @GraphQLClientApi(configKey = "order-api")
    public interface OrderApi {
        @Query
        Order order(String id);
    }

    @Inject OrderApi orders;

    @Query
    public Order order(String id) {
        return orders.order(id);
    }
}
