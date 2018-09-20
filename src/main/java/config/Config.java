package config;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.json.JsonObject;

public class Config {

    public static JsonObject mongoConfig() {
        return new JsonObject()
                       .put("connection_string", "mongodb://localhost:27017")
                       .put("db_name", "randomusers");//数据库名称

    }

    public static DeploymentOptions deployConfig() {
        return  new DeploymentOptions()
                        .setWorker(true)
                        .setInstances(10);
    }
}
