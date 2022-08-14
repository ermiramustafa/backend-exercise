package services;

import com.google.inject.Inject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import exceptions.RequestException;
import models.User;
import models.codecs.Content;
import mongo.IMongoDB;
import org.bson.types.ObjectId;
import play.libs.Json;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Http;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class DashboardContentService {
    @Inject
    HttpExecutionContext ec;
    @Inject
    IMongoDB mongoDB;

    public CompletableFuture<Content> save(Content content) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                MongoCollection collection = mongoDB.getMongoDatabase()
                        .getCollection("content", Content.class);
                content.setId(new ObjectId());
                collection.insertOne(content);
                return content;
            } catch (Exception ex) {
                throw new CompletionException(new RequestException(400, Json.toJson("Could not insert user!!!!!")));
            }
        }, ec.current());
    }

    public CompletableFuture<List<Content>> all(User user, String id) {
        return CompletableFuture.supplyAsync(() -> {
                    try {
                        MongoCollection<Content> collection = mongoDB
                                .getMongoDatabase()
                                .getCollection("content", Content.class);

                        collection.find(Filters.or(
                                        Filters.in("readACL", user.getId()),
                                        Filters.in("readACL", user.getRoles()),
                                        Filters.in("writeACL", user.getId()),
                                        Filters.in("writeACL", user.getRoles()),
                                        Filters.and(
                                                Filters.eq("readACL", new ArrayList<>()),
                                                Filters.eq("writeACL", new ArrayList<>()))
                                )
                        );
                        return collection
                                .find(Filters.eq("dashboardId",id))
                                .into(new ArrayList<>());
                    } catch (Exception e) {
                        throw new CompletionException(new RequestException(Http.Status.INTERNAL_SERVER_ERROR, "Something went wrong."));
                    }
                }
        );
    }


    public CompletableFuture<Content> update(Content content,String contentId, User user) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                MongoCollection<Content> collection = mongoDB.getMongoDatabase()
                        .getCollection("content", Content.class);

                collection.find(Filters.or(
                                Filters.in("readACL", user.getId()),
                                Filters.in("readACL", user.getRoles()),
                                Filters.in("writeACL", user.getId()),
                                Filters.in("writeACL", user.getRoles()),
                                Filters.and(
                                        Filters.eq("readACL", new ArrayList<>()),
                                        Filters.eq("writeACL", new ArrayList<>()))
                        )
                );
                collection.replaceOne(Filters.eq("_id", new ObjectId(contentId)), content);
                return content;
            } catch (Exception e) {
                throw new CompletionException(new RequestException(Http.Status.INTERNAL_SERVER_ERROR, e));
            }
        });
    }

    public CompletableFuture<Content> delete(Content content, String id, User user) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                MongoCollection<Content> collection = mongoDB
                        .getMongoDatabase()
                        .getCollection("content", Content.class);
                collection.find(Filters.or(
                                Filters.in("readACL", user.getId()),
                                Filters.in("readACL", user.getRoles()),
                                Filters.in("writeACL", user.getId()),
                                Filters.in("writeACL", user.getRoles()),
                                Filters.and(
                                        Filters.eq("readACL", new ArrayList<>()),
                                        Filters.eq("writeACL", new ArrayList<>()))
                        )
                );
                collection.deleteOne(Filters.eq("_id",  new ObjectId(id)));
                return content;
            } catch (Exception e) {
                e.printStackTrace();
                throw new CompletionException(new RequestException(Http.Status.INTERNAL_SERVER_ERROR, e));
            }
        });
    }




}
