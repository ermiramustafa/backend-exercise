package controllers;

import models.Dashboard;
import models.User;
import mongo.IMongoDB;
import play.mvc.Http;
import play.mvc.Result;
import com.google.inject.Inject;
import play.mvc.*;
import services.DashboardService;
import services.SerializationService;
import utils.DatabaseUtils;

import java.util.concurrent.CompletableFuture;

public class DashboardController extends Controller {

    @Inject
    IMongoDB mongo;

    @Inject
    SerializationService serializationService;

    @Inject
    DashboardService service;

    @BodyParser.Of(BodyParser.Json.class)
    public CompletableFuture<Result> save(Http.Request request, User user ) {
        return serializationService.parseBodyOfType(request, Dashboard.class)
                .thenCompose((data) -> service.save(user, data))
                .thenCompose((data) -> serializationService.toJsonNode(data))
                .thenApply(Results::ok)
                .exceptionally(DatabaseUtils::throwableToResult);
    }

    public CompletableFuture<Result> all(Http.Request request) {
        return serializationService.parseBodyOfType(request, User.class)
                .thenCompose((data) -> service.all(data))
                .thenCompose((data) -> serializationService.toJsonNode(data))
                .thenApply(Results::ok)
                .exceptionally(DatabaseUtils::throwableToResult);
    }
    public CompletableFuture<Result> update(Http.Request request) {
        return serializationService.parseBodyOfType(request, Dashboard.class)
                .thenCompose((data) -> service.update(data))
                .thenCompose((data) -> serializationService.toJsonNode(data))
                .thenApply(Results::ok)
                .exceptionally(DatabaseUtils::throwableToResult);
    }

    public CompletableFuture<Result> delete(Http.Request request, String id) {
        return serializationService.parseBodyOfType(request, Dashboard.class)
                .thenCompose((data) -> service.delete(data, id))
                .thenCompose((data) -> serializationService.toJsonNode(data))
                .thenApply(Results::ok)
                .exceptionally(DatabaseUtils::throwableToResult);
    }

}
