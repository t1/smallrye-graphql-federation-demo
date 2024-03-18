import {ApolloGateway} from "@apollo/gateway";
import {ApolloServer} from "@apollo/server";
import {startStandaloneServer} from "@apollo/server/standalone";

const gateway = new ApolloGateway({
    serviceList: [
        {name: 'product', url: 'http://localhost:18482/graphql'},
        {name: 'order', url: 'http://localhost:18483/graphql'},
        {name: 'price', url: 'http://localhost:18484/graphql'},
        // {name: 'stock', url: 'http://localhost:18485/graphql'},
    ],
});

const server = new ApolloServer({gateway});

const { url } = await startStandaloneServer(server, {
    context: async ({ req }) => ({ token: req.headers.token }),
    listen: { port: 4000 },
});
console.log(`🚀 GraphQL Gateway ready at ${url}`);
