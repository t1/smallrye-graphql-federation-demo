package org.example.order;

import io.smallrye.graphql.api.federation.Extends;
import io.smallrye.graphql.api.federation.External;
import io.smallrye.graphql.api.federation.Key;
import lombok.Value;
import org.eclipse.microprofile.graphql.Id;

@Value
@Extends
@Key(fields = "id")
public class Product {
    @External @Id String id;
}
