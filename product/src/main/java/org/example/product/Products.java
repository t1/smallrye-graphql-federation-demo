package org.example.product;

import jakarta.ws.rs.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Id;
import org.eclipse.microprofile.graphql.Query;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

@Slf4j
@GraphQLApi
public class Products {
    private static final Map<String, Product> PRODUCTS = new HashMap<>();

    static {
        add(Product.builder().id("1").name("Table").description("Elegant designer table with four legs").build());
        add(Product.builder().id("2").name("Chair").description("Nordic design chair with four legs").build());
    }

    private static void add(Product product) {
        PRODUCTS.put(product.getId(), product);
    }

    @Query
    public List<Product> products(List<String> ids) {
        log.info("get products({})", ids);
        var products = ids.stream().map(Products::get).collect(toList());
        log.info("found products: {}", products);
        return products;
    }

    @Query
    public Product product(@Id String id) {
        log.info("get product({})", id);
        var product = get(id);
        log.info("found product: {}", product);
        return product;
    }

    private static Product get(String id) {
        var product = PRODUCTS.get(id);
        if (product == null)
            throw new NotFoundException("product " + id + " not found");
        return product;
    }
}
