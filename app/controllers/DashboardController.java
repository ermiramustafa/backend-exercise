package controllers;

import actions.Authenticated;
import com.google.inject.Inject;
import models.Dashboard;
import mongo.IMongoDB;
import play.mvc.*;
import services.DashboardService;
import services.SerializationService;
import utils.DatabaseUtils;
import utils.ServiceUtils;

import java.util.concurrent.CompletableFuture;

@Authenticated
public class DashboardController extends Controller {

    @Inject
    IMongoDB mongo;

    @Inject
    SerializationService serializationService;

    @Inject
    DashboardService service;


    @BodyParser.Of(BodyParser.Json.class)
//    @Validation(type = Dashboard.class)
    public CompletableFuture<Result> save(Http.Request request) {
        return serializationService.parseBodyOfType(request, Dashboard.class)
                .thenCompose((data) -> service.save( data))
                .thenCompose((data) -> serializationService.toJsonNode(data))
                .thenApply(Results::ok)
                .exceptionally(DatabaseUtils::throwableToResult);
    }

//    public CompletableFuture<Result> all(Http.Request request) {
//        return serializationService.parseBodyOfType(request, User.class)
//                .thenCompose((data) -> service.all(data))
//                .thenCompose((data) -> serializationService.toJsonNode(data))
//                .thenApply(Results::ok)
//                .exceptionally(DatabaseUtils::throwableToResult);
//    }

//    @Authenticated
    public CompletableFuture<Result> all(Http.Request request, int limit, int skip) {
        return service.all(ServiceUtils.getUserFrom(request), limit, skip)
                .thenCompose((data) -> serializationService.toJsonNode(data))
                .thenApply(Results::ok)
                .exceptionally(DatabaseUtils::throwableToResult);
    }
    //@Validation(type = Dashboard.class)
    public CompletableFuture<Result> update(Http.Request request, String id) {
        System.out.println("Testcontro");
        return serializationService.parseBodyOfType(request, Dashboard.class)
                .thenCompose(data -> service.update(data, id, ServiceUtils.getUserFrom(request)))
                .thenCompose(data -> serializationService.toJsonNode(data))
                .thenApply(Results::ok)
                .exceptionally(e -> {
                    e.printStackTrace();
                    return DatabaseUtils.throwableToResult(e);
                });

    }

    public CompletableFuture<Result> delete(Http.Request request, String id) {
        return serializationService.parseBodyOfType(request, Dashboard.class)
                .thenCompose(data -> service.delete(data, id, ServiceUtils.getUserFrom(request)))
                .thenCompose(data -> serializationService.toJsonNode(data))
                .thenApply(Results::ok)
                .exceptionally(DatabaseUtils::throwableToResult);
    }

//    public CompletableFuture<Result> hierarchy(Http.Request request) {
//        return  service.hierarchy()
//                .thenCompose((data) -> serializationService.toJsonNode(data))
//                .thenApply(Results::ok)
//                .exceptionally(DatabaseUtils::throwableToResult);
//    }

    @Authenticated
    public CompletableFuture<Result> hierarchy() {
        return service.hierarchy()
                .thenCompose((data) -> serializationService.toJsonNode(data))
                .thenApply(Results::ok)
                .exceptionally(e -> {
                    e.printStackTrace();
                    return DatabaseUtils.throwableToResult(e);
                });
    }
}
