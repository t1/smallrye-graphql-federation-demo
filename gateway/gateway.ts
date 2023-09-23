import {ApolloGateway} from "@apollo/gateway";
import {ApolloServer, ServerInfo} from "apollo-server";
import {ApolloServerPluginLandingPageGraphQLPlayground} from "apollo-server-core";

const gateway = new ApolloGateway({
    serviceList: [
        {name: 'product', url: 'http://localhost:18482/graphql'},
        {name: 'order', url: 'http://localhost:18483/graphql'},
        {name: 'price', url: 'http://localhost:18484/graphql'},
        // {name: 'stock', url: 'http://localhost:18485/graphql'},
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
