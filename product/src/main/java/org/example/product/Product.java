package org.example.product;

import io.smallrye.graphql.federation.api.Key;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import org.eclipse.microprofile.graphql.Description;

@Description("Something you can buy")
@Data @SuperBuilder
@Key(fields = "id")
public class Product {
    private String id;
    private String name;
    private String description;

    @Description("The price in cent")
    private Integer price;
}
