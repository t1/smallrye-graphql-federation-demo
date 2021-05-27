package org.example.bff;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.List;

@Data @SuperBuilder @NoArgsConstructor
public class Order {
    private LocalDate orderDate;
    private String orderNumber;
    private String customerNumber;
    private String customerName;

    private @Singular List<OrderItem> orderItems;
}
