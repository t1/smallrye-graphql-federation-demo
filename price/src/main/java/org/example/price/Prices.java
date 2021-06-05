package org.example.price;

import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Id;
import org.eclipse.microprofile.graphql.Query;
import org.eclipse.microprofile.graphql.Source;

import java.util.Map;

@GraphQLApi
public class Prices {
    private static final Map<String, Integer> PRICES = Map.of("1", 399_99, "2", 199_99);

    @SuppressWarnings("unused")
    public Integer price(@Source Product product) {return PRICES.get(product.getId());}

    @Query
    public Product product(@Id String id) {return Product.builder().id(id).price(PRICES.get(id)).build();}
}
