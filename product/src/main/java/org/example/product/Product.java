package org.example.product;

import lombok.Data;
import lombok.experimental.SuperBuilder;
import org.eclipse.microprofile.graphql.Description;

@Description("Something you can buy")
@Data @SuperBuilder
public class Product {
    private String id;
    private String name;
    private String description;

    @Description("The price in cent")
    private Integer price;
}
