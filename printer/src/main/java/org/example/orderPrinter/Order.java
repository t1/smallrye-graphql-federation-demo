package org.example.orderPrinter;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.List;
import java.util.function.ToLongFunction;

@Data @SuperBuilder @NoArgsConstructor
public class Order {
    private LocalDate orderDate;
    private String orderNumber;
    private String customerNumber;
    private String customerName;

    private @Singular List<OrderItem> orderItems;


    long amountSum() {
        return sum(OrderItem::getAmount);
    }

    long priceSum() {
        return sum(OrderItem::getTotalPrice);
    }

    private long sum(ToLongFunction<OrderItem> property) {
        return getOrderItems().stream().mapToLong(property).sum();
    }
}
