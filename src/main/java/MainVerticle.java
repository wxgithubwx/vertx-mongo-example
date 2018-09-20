import config.Config;
import io.vertx.core.AbstractVerticle;

public class MainVerticle extends AbstractVerticle {

    @Override
    public void start() throws Exception {

        vertx.deployVerticle("verticles.RandomUserClient");
        vertx.deployVerticle("verticles.RestApiVerticle", Config.deployConfig());

    }
}
