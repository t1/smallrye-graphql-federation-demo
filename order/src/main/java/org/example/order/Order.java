package org.example.order;

import io.smallrye.graphql.api.federation.FieldSet;
import io.smallrye.graphql.api.federation.Key;
import lombok.Data;
import lombok.Singular;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.List;

@Data @SuperBuilder
@Key(fields = @FieldSet("id"))
public class Order {
    private String id;
    private LocalDate orderDate;
    private String orderNumber;
    private String customerNumber;
    private String customerName;

    private @Singular List<OrderItem> orderItems;
}
