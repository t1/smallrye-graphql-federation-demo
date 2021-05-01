package org.example.product;

import lombok.Data;
import lombok.experimental.SuperBuilder;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Something you can buy")
@Data @SuperBuilder
public class Product {
    private String id;
    private String name;
    private String description;

    @Schema(description = "The price in cent")
    private Integer price;
}
