package org.example.order;

import lombok.Data;
import lombok.Singular;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.List;

@Data @SuperBuilder
public class Order {
    private String id;
    private LocalDate orderDate;
    private String orderNumber;
    private String customerNumber;
    private String customerName;

    private @Singular List<OrderItem> orderItems;
}
