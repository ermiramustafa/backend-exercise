package controllers;

import actions.Validation;
import com.google.inject.Inject;
import models.User;
import mongo.IMongoDB;
import play.data.validation.Constraints;
import play.mvc.BodyParser;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;
import services.SerializationService;
import services.UserService;
import utils.DatabaseUtils;

import java.util.concurrent.CompletableFuture;

public class UserController {
    @Inject
    IMongoDB mongoDB;

    @Inject
    UserService userService;

    @Inject
    SerializationService serializationService;
    public CompletableFuture<Result> all(Http.Request request) {
        return userService.all()
                .thenCompose(users -> serializationService.toJsonNode(users))
                .thenApply(Results::ok)
                .exceptionally(DatabaseUtils::throwableToResult);
    }

    public CompletableFuture<Result> userById(Http.Request request, String id) {
        return userService.userById(id)
                .thenCompose(user -> serializationService.toJsonNode(user))
                .thenApply(Results::ok)
                .exceptionally(DatabaseUtils::throwableToResult);
    }

    public CompletableFuture<Result> delete (Http.Request request, String id) {
        return userService.delete(id)
                .thenCompose(user -> serializationService.toJsonNode(user))
                .thenApply(Results::ok)
                .exceptionally(DatabaseUtils::throwableToResult);
    }

    @BodyParser.Of(BodyParser.Json.class)
    @Validation(type = User.class)
    public CompletableFuture<Result> update (Http.Request request, String id) {
        return serializationService.parseBodyOfType(request, User.class)
                .thenCompose(user -> userService.update(id, user))
                .thenCompose(user -> serializationService.toJsonNode(user))
                .thenApply(Results::ok)
                .exceptionally(DatabaseUtils::throwableToResult);
    }
    @BodyParser.Of(BodyParser.Json.class)
    @Validation(type = User.class)
    public CompletableFuture<Result> save(Http.Request request) {
        return serializationService.parseBodyOfType(request, User.class)
                .thenCompose((data) -> userService.save(data))
                .thenCompose((data) -> serializationService.toJsonNode(data))
                .thenApply(Results::ok)
                .exceptionally(DatabaseUtils::throwableToResult);
    }



}
