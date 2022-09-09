package org.example.price;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Id;
import org.eclipse.microprofile.graphql.Query;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

@Slf4j
@GraphQLApi
public class Prices {
    private static final Map<String, Integer> PRICES = Map.of("1", 399_99, "2", 199_99);

    @Query
    public List<Product> products(List<String> ids) {
        log.info("get prices for products({})", ids);
        var products = ids.stream().map(Prices::get).collect(toList());
        log.info("found prices: {}", products);
        return products;
    }

    @Query
    public Product product(@Id String id) {
        log.info("get price for product({})", id);
        var product = get(id);
        log.info("found price: {}", product);
        return product;
    }

    private static Product get(String id) {
        return Product.builder().id(id).price(PRICES.get(id)).build();
    }
}
