package services;

import com.google.inject.Inject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import exceptions.RequestException;
import models.User;
import mongo.IMongoDB;
import org.bson.types.ObjectId;
import play.libs.Json;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Http;
import utils.Hash;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static play.mvc.Http.Status.NOT_FOUND;

public class UserService {

    @Inject
    HttpExecutionContext ec;

    @Inject
    IMongoDB mongoDB;

    /**
     * Function that returns all users
     *
     * @param
     * @return
     */
    public CompletableFuture<List<User>> all() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return mongoDB.getMongoDatabase()
                        .getCollection("users", User.class)
                        .find()
                        .into(new ArrayList<>());

            } catch (Exception ex) {
                ex.printStackTrace();
                throw new CompletionException(new RequestException(Http.Status.BAD_REQUEST, Json.toJson("Could not fetch data!")));
            }
        }, ec.current());
    }


    /**
     * Function that returns a user by a given Id
     *
     * @param id
     * @return user
     */
    public CompletableFuture<User> userById(String id) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                if (!ObjectId.isValid(id)) {
                    throw new RequestException(Http.Status.BAD_REQUEST, "Bad Request!");
                }
                User u1 = mongoDB.getMongoDatabase()
                        .getCollection("users", User.class)
                        .find()
                        .filter(Filters.eq("_id", new ObjectId(id)))
                        .first();
                if (u1 == null) {
                    throw new RequestException(NOT_FOUND, "Document with id: " + id + " not found!");
                }
                return u1;

            } catch (Exception ex) {
                throw new CompletionException(new RequestException(Http.Status.BAD_REQUEST, Json.toJson("Could not fetch data!")));
            }
        }, ec.current());
    }

    /**
     * Function that is used to delete a user
     *
     * @param id
     * @return deletedUser
     */
    public CompletableFuture<User> delete(String id) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                if (!ObjectId.isValid(id)) {
                    throw new RequestException(Http.Status.BAD_REQUEST, "Bad Request!");
                }

                User user = mongoDB.getMongoDatabase()
                        .getCollection("users", User.class)
                        .findOneAndDelete(Filters.eq("_id", new ObjectId(id)));

                if (user == null) {
                    throw new RequestException(NOT_FOUND, "Document with id: " + id + " not found!");
                }
                return user;
            } catch (Exception ex) {
                throw new CompletionException(new RequestException(Http.Status.BAD_REQUEST, Json.toJson("Could not fetch data!")));
            }
        }, ec.current());
    }

    /**
     * Function that updates a user
     *
     * @param id, user
     * @return UpdatedUser
     */
    public CompletableFuture<User> update(String id, User user) {
        return CompletableFuture.supplyAsync(() ->
        {
            try {
                System.out.println("Testo testo testo");
                if (!ObjectId.isValid(id)) {
                    throw new RequestException(Http.Status.BAD_REQUEST, "Bad Request!");
                }
                return mongoDB.getMongoDatabase()
                        .getCollection("users", User.class)
                        .findOneAndReplace(Filters.eq("_id", new ObjectId(id)), user);
            } catch (Exception ex) {
                throw new CompletionException(new RequestException(Http.Status.BAD_REQUEST, Json.toJson("Could not fetch data!")));
            }
        }, ec.current());
    }

    /**
     * Function that saves a user
     *
     * @param user
     * @return newUser
     */
    public CompletableFuture<User> save(User user) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                MongoCollection<User> collection = mongoDB.getMongoDatabase()
                        .getCollection("users", User.class);
//                user.setId(new ObjectId());
                user.setPassword(Hash.createPassword(user.getPassword()));
                collection.insertOne(user);
                return user;

            } catch (Exception ex) {
                throw new CompletionException(new RequestException(400, Json.toJson("Could not insert user!")));
            }
        }, ec.current());
    }


//    public CompletableFuture<List<ObjectId>> getUserACL(String username) {
//        MongoCollection<User> collection =  mongoDB.getMongoDatabase().getCollection("users", User.class);
//        User user = collection.find(eq("username", username))
//                .first();
//        return CompletableFuture.supplyAsync(() -> {
//            try {
//                List<ObjectId> res = new ArrayList<>();
//                res.add(user.getId());
//                res.add(user.getRoles());
//                return res;
//            } catch (NullPointerException ex) {
//                throw new CompletionException(new RequestException(NOT_FOUND, "User doesnt exeist"));
//            }
//        }, ec.current());
//    }

}
