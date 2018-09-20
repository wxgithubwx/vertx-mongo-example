package verticles;

import config.Config;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.FindOptions;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.junit.Test;

import java.util.Random;

public class RestApiVerticle extends AbstractVerticle {

    private Router router;
    private MongoClient mongoClient;

    private final String DB_NAME = "randomusers";


    @Override
    public void start() throws Exception {
        Vertx vertx=Vertx.vertx();

        router = Router.router(vertx);
        mongoClient = MongoClient.createShared(vertx, Config.mongoConfig());

        router.route("/api/all-users").handler(this::getAllUsers);
        router.route("/api/random-user").handler(this::getRandomUser);
        router.route("/api/get-users").handler(this::getUsers);

        vertx.createHttpServer().requestHandler(router::accept).listen(8080);
    }

    private void getAllUsers(RoutingContext ctx) {
        HttpServerResponse response = ctx.response();

        mongoClient.find(DB_NAME, new JsonObject(), resp -> {
            if (resp.succeeded()) {
                response.setStatusCode(200);
                response.setChunked(true);
                response.putHeader("content-type", "application/json");
                response.putHeader("list-size", String.valueOf(resp.result().size()));
                response.end(resp.result().toString());
            } else {
                response.setStatusCode(404).end("Mongo crash!");
            }
        });
    }

    private void getRandomUser(RoutingContext ctx) {
        HttpServerResponse response = ctx.response();
        FindOptions randomOptions = new FindOptions();

        mongoClient.count(DB_NAME, new JsonObject(), resCount -> {
            if (resCount.succeeded()) {
                long count = resCount.result();
                randomOptions.setLimit(1).setSkip(new Random().nextInt(Math.toIntExact(count)));

                mongoClient.findBatchWithOptions(DB_NAME, new JsonObject(), randomOptions, res -> {
                    if (res.succeeded()) {
                        response.setStatusCode(200);
                        response.end(res.result().encodePrettily());
                    } else {
                        response.setStatusCode(404).end("Mongo crash!");
                    }
                });
            } else {
                response.setStatusCode(404).end("Mongo crash!");
            }
        });

    }

    private void getUsers(RoutingContext ctx) {
        HttpServerResponse response = ctx.response();
        HttpServerRequest request = ctx.request();
        FindOptions options = new FindOptions();
        Buffer buffer = Buffer.buffer();

        int size = Integer.valueOf(request.getParam("count"));
        options.setLimit(size);


        mongoClient.findWithOptions(DB_NAME, new JsonObject(), options, res -> {
            if (res.succeeded()) {
                res.result().stream()
                        .forEach(o -> buffer.appendString(o.encodePrettily()).appendString("\n"));
                response.setStatusCode(200).end(buffer);
            } else {
                response.setStatusCode(404).end("Mongo crash!");
            }
        });

    }
}
