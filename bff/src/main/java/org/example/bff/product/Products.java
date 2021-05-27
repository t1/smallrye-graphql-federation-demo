package org.example.bff.product;

import io.smallrye.graphql.client.typesafe.api.GraphQLClientApi;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Query;
import org.eclipse.microprofile.graphql.Source;
import org.example.bff.order.OrderItem;

import javax.inject.Inject;

@GraphQLApi
public class Products {
    @GraphQLClientApi(configKey = "product-api")
    public interface ProductApi {
        @Query
        Product product(String id);
    }

    @Inject ProductApi products;

    public Product product(@Source OrderItem orderItem) {
        return products.product(orderItem.getProductId());
    }
}
