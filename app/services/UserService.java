package services;

import com.google.inject.Inject;
import com.mongodb.client.model.Filters;
import exceptions.RequestException;
import models.User;
import mongo.IMongoDB;
import play.libs.concurrent.HttpExecutionContext;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static play.mvc.Http.Status.NOT_FOUND;

public class UserService {

    @Inject
    HttpExecutionContext ec;

    @Inject
    IMongoDB mongoDB;



//    public CompletableFuture<List<User>> all() {
//        return CompletableFuture.supplyAsync(() -> mongoDB
//                .getMongoDatabase()
//                .getCollection("users", User.class)
//                .find()
//                .into(new ArrayList<>()), ec.current());
//    }
    @Inject
    DBservice dbService;

    private static final String COLLECTION_NAME = "users-exercise";

    public CompletableFuture<User> find(User request) {
        return dbService.find(User.class, Filters.and(Filters.eq("username", request.getUsername()), Filters.eq("password", request.getPassword())), COLLECTION_NAME)
                .thenApply(result -> {
                    if(result == null) {
                        throw new CompletionException(new RequestException(NOT_FOUND, "Check again"));
                    }
                    return result;
                });
    }
}
