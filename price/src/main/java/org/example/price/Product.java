package org.example.price;

import io.smallrye.graphql.api.federation.Extends;
import io.smallrye.graphql.api.federation.FieldSet;
import io.smallrye.graphql.api.federation.Key;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.eclipse.microprofile.graphql.Description;
import org.eclipse.microprofile.graphql.Id;

@Data @SuperBuilder @NoArgsConstructor
@Extends @Key(fields = @FieldSet("id"))
public class Product {
    @Id String id;
    @Description("The price in cent")
    Integer price;
}
