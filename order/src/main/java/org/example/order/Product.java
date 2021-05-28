package org.example.order;

import io.smallrye.graphql.federation.api.Extends;
import io.smallrye.graphql.federation.api.External;
import lombok.Value;

@Value
@Extends
public class Product {
    @External String id;
}
