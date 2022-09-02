package org.example.price;

import io.smallrye.graphql.api.federation.Extends;
import io.smallrye.graphql.api.federation.External;
import io.smallrye.graphql.api.federation.Key;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.eclipse.microprofile.graphql.Description;
import org.eclipse.microprofile.graphql.Id;

@Data @SuperBuilder @NoArgsConstructor
@Extends @Key(fields = "id")
public class Product {
    @External @Id String id;
    @Description("The price in cent")
    Integer price;
}
