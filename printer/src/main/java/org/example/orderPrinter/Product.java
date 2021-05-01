package org.example.orderPrinter;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data @SuperBuilder @NoArgsConstructor
public class Product {
    private String name;
    private Long price;
}
