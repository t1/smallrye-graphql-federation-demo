package org.example.order;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data @SuperBuilder
public class OrderItem {
    private String id;
    private Integer position;
    private Integer amount;
    private String productId;
}
