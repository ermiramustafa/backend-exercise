package controllers;

import mongo.IMongoDB;
import org.bson.Document;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import services.SerializationService;
import services.UserService;

import javax.inject.Inject;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class HomeController extends Controller {
    @Inject
    IMongoDB mongoDB;

    @Inject
    UserService userService;

    @Inject
    SerializationService serializationService;
    /**
     * An action that renders an HTML page with a welcome message.
     * The configuration in the <code>routes</code> file means that
     * this method will be called when the application receives a
     * <code>GET</code> request with a path of <code>/</code>.
     */
    public Result index() {
        return ok(views.html.index.render());
    }

    public Result mongoTest(Http.Request request) {
        Document document = Json.fromJson(request.body().asJson(), Document.class);
        mongoDB.getMongoDatabase().getCollection("firstCollection").insertOne(document);
        return ok(Json.toJson(document));
    }

//    public CompletableFuture<Result> findAllUsers(Http.Request request) {
//        return userService.findAllUsers()
//                .thenCompose(users -> serializationService.toJsonNode(users))
//                .thenApply(Results::ok)
//                .exceptionally(DatabaseUtils::throwableToResult);
//    }
}
