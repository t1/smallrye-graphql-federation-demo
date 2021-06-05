package org.example.product;

import io.smallrye.graphql.api.federation.Key;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import org.eclipse.microprofile.graphql.Description;
import org.eclipse.microprofile.graphql.Id;

@Description("Something you can buy")
@Data @SuperBuilder
@Key(fields = "id")
public class Product {
    @Id private String id;
    private String name;
    private String description;
}
