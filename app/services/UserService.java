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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static com.mongodb.client.model.Filters.eq;
import static play.mvc.Http.Status.NOT_FOUND;

public class UserService {

    @Inject
    HttpExecutionContext ec;

    @Inject
    IMongoDB mongoDB;

    public CompletableFuture<List<User>> all () {
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

    public CompletableFuture<User> userById(String id) {
        return CompletableFuture.supplyAsync(() -> {
            try{
                if(!ObjectId.isValid(id)) {
                    throw new RequestException(Http.Status.BAD_REQUEST, "invalid hexadecimal representation of an ObjectId: [" + id + "]");
                }
                User u1 =  mongoDB.getMongoDatabase()
                        .getCollection("users", User.class)
                        .find()
                        .filter(Filters.eq("_id", new ObjectId(id)))
                        .first();
                if (u1 == null) {
                    throw new RequestException(NOT_FOUND, "Document with id: " + id + " not found!");
                }
                return u1;

            }catch(Exception ex) {
                throw new CompletionException(new RequestException(Http.Status.BAD_REQUEST, Json.toJson("Could not fetch data!")));
            }
        }, ec.current());
    }


    public CompletableFuture<User> delete(String id) {
        return CompletableFuture.supplyAsync(() -> {
            try{
                if(!ObjectId.isValid(id)) {
                    throw new RequestException(Http.Status.BAD_REQUEST, "invalid hexadecimal representation of an ObjectId: [" + id + "]");
                }

                User user= mongoDB.getMongoDatabase()
                        .getCollection("users", User.class)
                        .findOneAndDelete(Filters.eq("_id", new ObjectId(id)));

                if(user == null){
                    throw new RequestException(NOT_FOUND, "Document with id: " + id + " not found!");
                }
                return user;
            }
            catch(Exception ex) {
                throw new CompletionException(new RequestException(Http.Status.BAD_REQUEST, Json.toJson("Could not fetch data!")));
            }
        },ec.current());
    }

    public CompletableFuture<User> update (String id, User user) {
        return CompletableFuture.supplyAsync(() ->
        {
            try{
                if(!ObjectId.isValid(id)) {
                    throw new RequestException(Http.Status.BAD_REQUEST,
                            String.format("Invalid hexadecimal representation of an ObjectId: [%s]", id));
                }
               return mongoDB.getMongoDatabase()
                        .getCollection("users", User.class)
                        .findOneAndReplace(Filters.eq("_id", new ObjectId(id)), user);
            }catch(Exception ex) {
                throw new CompletionException(new RequestException(Http.Status.BAD_REQUEST, Json.toJson("Could not fetch data!")));
            }
        },ec.current());
    }

    public CompletableFuture<User> save(User user) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                MongoCollection<User> collection = mongoDB.getMongoDatabase()
                        .getCollection("users", User.class);
                user.setId(new ObjectId());
                collection.insertOne(user);
                return user;

            } catch (Exception ex) {
                throw new CompletionException(new RequestException(400, Json.toJson("Could not insert user!")));
            }
        }, ec.current());
    }


    public CompletableFuture<List<ObjectId>> getUserACL(String username) {
        MongoCollection<User> collection =  mongoDB.getMongoDatabase().getCollection("users", User.class);
        User user = collection.find(eq("username", username))
                .first();
        return CompletableFuture.supplyAsync(() -> {
            try {
                List<ObjectId> res = new ArrayList<>();
                res.add(user.getId());
                user.getRoles().forEach(x -> res.add(x.getId()));
                return res;
            } catch (NullPointerException ex) {
                throw new CompletionException(new RequestException(NOT_FOUND, "Credentials are incorrect or the user doesn't exist!"));
            }
        }, ec.current());
    }

}
