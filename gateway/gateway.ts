import {ApolloGateway} from "@apollo/gateway";
import {ApolloServer, ServerInfo} from "apollo-server";
import {ApolloServerPluginLandingPageGraphQLPlayground} from "apollo-server-core";

const gateway = new ApolloGateway({
    serviceList: [
        {name: 'product', url: 'http://localhost:8082/graphql'},
        {name: 'order', url: 'http://localhost:8083/graphql'},
        {name: 'price', url: 'http://localhost:8084/graphql'},
        // {name: 'stock', url: 'http://localhost:8085/graphql'},
    ],
});

const server = new ApolloServer({
    gateway,
    plugins: [
        ApolloServerPluginLandingPageGraphQLPlayground(),
    ],
});

server.listen().then((serverInfo: ServerInfo) => {
    console.log(`🚀 Gateway ready on ${serverInfo.url}`);
});
