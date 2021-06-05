const {ApolloServer} = require('apollo-server');
const {ApolloGateway} = require('@apollo/gateway');

const gateway = new ApolloGateway({
    serviceList: [
        {name: 'product', url: 'http://127.0.0.1:8082/graphql'},
        {name: 'order', url: 'http://127.0.0.1:8083/graphql'},
        {name: 'price', url: 'http://127.0.0.1:8084/graphql'},
    ],
});

const server = new ApolloServer({
    gateway,
    subscriptions: false,
    tracing: true
});

server.listen().then(({url}) => {
    console.log(`🚀 Gateway ready at ${url}`);
});
