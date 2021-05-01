package org.example.product;

import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import java.util.HashMap;
import java.util.Map;

@Path("/products")
public class Products {
    private static final Map<String, Product> PRODUCTS = new HashMap<>();

    static {
        add(Product.builder().id("1").name("Table").description("Elegant designer table with four legs").price(399_99).build());
        add(Product.builder().id("2").name("Chair").description("Nordic design chair with four legs").price(199_99).build());
    }

    private static void add(Product product) {
        PRODUCTS.put(product.getId(), product);
    }

    @GET @Path("/{id}")
    public Product product(@PathParam("id") String id) {
        var product = PRODUCTS.get(id);
        if (product == null)
            throw new NotFoundException("product " + id + " not found");
        return product;
    }
}
