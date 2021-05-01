package org.example.orderPrinter;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data @SuperBuilder @NoArgsConstructor
public class OrderItem {
    private Integer position;
    private Integer amount;
    private String productId;
    private Product product;

    public long getTotalPrice() {
        return amount * product.getPrice();
    }
}
