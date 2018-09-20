package verticles;

import config.Config;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.codec.BodyCodec;

import java.util.concurrent.TimeUnit;

public class RandomUserClient extends AbstractVerticle {

    private WebClient client;
    private MongoClient mongoClient;

    @Override
    public void start() throws Exception {
        client = WebClient.create(vertx);

        mongoClient = MongoClient.createShared(vertx, Config.mongoConfig());

        long longTime = TimeUnit.MILLISECONDS.convert(10, TimeUnit.MINUTES);

        vertx.setPeriodic(longTime, v -> {
            client.getAbs("https://randomuser.me/api/?results=10")
                    .as(BodyCodec.jsonObject())
                    .send(ar -> {
                        if (ar.succeeded()) {
                            JsonArray randomUsersList = ar.result().body().getJsonArray("results");
                            randomUsersList.stream().forEach(t -> {
                                mongoClient.save("randomusers", (JsonObject) t, s -> {

                                });
                            });

                        } else {
                            ar.cause().printStackTrace();
                        }
                    });
        });



    }
}
