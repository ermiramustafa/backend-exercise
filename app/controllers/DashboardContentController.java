package controllers;

import actions.Authenticated;
import com.google.inject.Inject;
import models.codecs.Content;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;
import services.DashboardContentService;
import services.SerializationService;
import utils.DatabaseUtils;
import utils.ServiceUtils;

import java.util.concurrent.CompletableFuture;

@Authenticated
public class DashboardContentController extends Controller {
    @Inject
    SerializationService serializationService;

    @Inject
    DashboardContentService dcService;

    public CompletableFuture<Result> all(Http.Request request, String id) {
        return dcService.all(ServiceUtils.getUserFrom(request), id)
                .thenCompose((data) -> serializationService.toJsonNode(data))
                .thenApply(Results::ok)
                .exceptionally(DatabaseUtils::throwableToResult);
    }

//    @Validation(type = Content.class)
    public CompletableFuture<Result> save(Http.Request request) {
        return serializationService.parseBodyOfType(request, Content.class)
                .thenCompose(data -> dcService.save(data))
                .thenCompose(data -> serializationService.toJsonNode(data))
                .thenApply(Results::ok)
                .exceptionally(DatabaseUtils::throwableToResult);
    }

//    @Validation(type = Content.class)
    public CompletableFuture<Result> update(Http.Request request, String id) {
        return serializationService.parseBodyOfType(request, Content.class)
                .thenCompose((data) -> dcService.update(data, id, ServiceUtils.getUserFrom(request)))
                .thenCompose((data) -> serializationService.toJsonNode(data))
                .thenApply(Results::ok)
                .exceptionally(DatabaseUtils::throwableToResult);
    }

    public CompletableFuture<Result> delete(Http.Request request, String id) {
        return serializationService.parseBodyOfType(request, Content.class)
                .thenCompose(data -> dcService.delete(data, id,ServiceUtils.getUserFrom(request)))
                .thenCompose(data -> serializationService.toJsonNode(data))
                .thenApply(Results::ok)
                .exceptionally(DatabaseUtils::throwableToResult);
    }

}
