# vertx-mongo-example

# Requirements
- Install MongoDB and running
- Vertx

# Basic Usage
This application uses [random user api](https://randomuser.me/).

Every 10 munites fetch to random user api and save the mongodb.

The api responses the client, fetch data from mongodb.

- all-users

[`http://localhost:8080/api/all-users`](http://localhost:8080/api/all-users)

- random-user

[`http://localhost:8080/api/random-user`](http://localhost:8080/api/random-user)

- get-users by count

[`http://localhost:8080//api/get-users?count=number`](http://localhost:8080//api/get-users?count=12)
