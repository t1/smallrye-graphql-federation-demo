package org.example.order;

import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Query;
import org.eclipse.microprofile.graphql.Source;

import javax.ws.rs.NotFoundException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@GraphQLApi
public class Orders {
    private static final Map<String, Order> ORDERS = new HashMap<>();

    static {
        add(Order.builder().id("1")
            .orderNumber("21/8820.1756")
            .orderDate(LocalDate.of(2021, 6, 9))
            .customerNumber("123456")
            .customerName("Jane Doe")
            .orderItem(OrderItem.builder()
                .id("1001")
                .position(1)
                .amount(1)
                .productId("1")
                .build())
            .orderItem(OrderItem.builder()
                .id("1002")
                .position(2)
                .amount(4)
                .productId("2")
                .build())
            .build());
    }

    private static void add(Order order) {
        ORDERS.put(order.getId(), order);
    }

    @Query
    public Order order(String id) {
        var order = ORDERS.get(id);
        if (order == null)
            throw new NotFoundException("order " + id + " not found");
        return order;
    }

    public Product product(@Source OrderItem orderItem) {
        return new Product(orderItem.getProductId());
    }
}
