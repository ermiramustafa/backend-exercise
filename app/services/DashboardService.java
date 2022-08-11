package services;


import com.google.inject.Inject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.InsertOneResult;
import exceptions.RequestException;
import models.Dashboard;
import models.User;
import models.codecs.Content;
import mongo.IMongoDB;
import org.bson.BsonNull;
import org.bson.Document;
import play.libs.Json;
import play.libs.concurrent.HttpExecutionContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.stream.Collectors;

public class DashboardService {
    @Inject
    HttpExecutionContext ec;
    @Inject
    IMongoDB mongoDB;

    public CompletableFuture<Dashboard> save(Dashboard dashboard) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                MongoCollection collection = mongoDB.getMongoDatabase()
                        .getCollection("dashboards", Dashboard.class);
//                dashboard.getReadACL().add(user.getId().toString());
//                dashboard.getWriteACL().add(user.getId().toString());
                InsertOneResult toReturn = collection.insertOne(dashboard);
                if (!toReturn.wasAcknowledged() || toReturn.getInsertedId() == null) {
                    throw new CompletionException(new RequestException(400, Json.toJson("Could not insert user!!!!!")));
                }
                return dashboard;
            } catch (Exception ex) {
                throw new CompletionException(new RequestException(400, Json.toJson("Could not insert user!!!!!")));
            }
        }, ec.current());
    }

    public CompletableFuture<List<Dashboard>> all(User user) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                int limit = 100, skip = 0;
                MongoCollection<Dashboard> collection = mongoDB
                        .getMongoDatabase()
                        .getCollection("dashboard", Dashboard.class);
                List<Dashboard> dashboards = collection.find(Filters.or(
                                        Filters.in("readACL", user.getId()),
                                        Filters.in("readACL", user.getRoles()),
                                        Filters.in("writeACL", user.getId()),
                                        Filters.in("writeACL", user.getRoles()),
                                        Filters.and(
                                                Filters.eq("readACL", new ArrayList<>()),
                                                Filters.eq("writeACL", new ArrayList<>()))

                                )
                        )
                        .skip(skip)
                        .limit(limit)
                        .into(new ArrayList<>());
                List<String> dashId = dashboards.stream().map(x -> x.getId().toString()).collect(Collectors.toList());
                List<Content> contents = mongoDB.getMongoDatabase()
                        .getCollection("content", Content.class)
                        .find(Filters.and(
                                Filters.in("dashboardId", dashId),
                                Filters.or(
                                        Filters.in("readACL", user.getId()),
                                        Filters.in("readACL", user.getRoles()),
                                        Filters.in("writeACL", user.getId()),
                                        Filters.in("writeACL", user.getRoles()),
                                        Filters.and(
                                                Filters.eq("readACL", new ArrayList<>()),
                                                Filters.eq("writeACL", new ArrayList<>()))

                                )))
                        .into(new ArrayList<>());

                dashboards.forEach(dashboard -> dashboard
                        .setContent(
                                contents
                                        .stream().filter(x -> x.getId().equals(dashboard.getId())).collect(Collectors.toList())));
                return dashboards;
            } catch (Exception ex) {
                ex.printStackTrace();
                throw new CompletionException(new RequestException(400, Json.toJson("Something is wrong")));
            }
        }, ec.current());
    }

    public CompletableFuture<Dashboard> update(Dashboard dashboard) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                MongoCollection<Dashboard> collection = mongoDB.getMongoDatabase()
                        .getCollection("dashboards", Dashboard.class);

                FindIterable<Dashboard> toReturn = collection.find(Filters.eq("_id", dashboard.getId()));
                Dashboard finalDashboard = toReturn.first();
                if (finalDashboard == null) {
                    throw new CompletionException(new RequestException(400, Json.toJson("Couldn't insert user")));
                }
                dashboard.getReadACL().addAll(finalDashboard.getReadACL());
                dashboard.getWriteACL().addAll(finalDashboard.getWriteACL());
                collection.replaceOne(Filters.eq("_id", dashboard.getId()), dashboard);
                return dashboard;
            } catch (NullPointerException | IllegalAccessError ex) {
                throw new CompletionException(new RequestException(404, Json.toJson("USer not found")));
            } catch (Exception ex) {
                throw new CompletionException(new RequestException(400, Json.toJson("Couldn't insert user")));
            }
        }, ec.current());
    }

    public CompletableFuture<Dashboard> delete(Dashboard dashboard, String id) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                MongoCollection<Dashboard> collection = mongoDB.getMongoDatabase()
                        .getCollection("dashboards", Dashboard.class);

                FindIterable<Dashboard> toReturn = collection.find(Filters.eq("_id", dashboard.getId()));
                Dashboard finalDashboard = toReturn.first();
                if (finalDashboard == null) {
                    throw new CompletionException(new RequestException(400, Json.toJson("Couldn't insert user")));
                }
                collection.deleteOne(Filters.eq("_id", dashboard.getId()));
                return dashboard;

            } catch (NullPointerException | IllegalArgumentException ex) {
                throw new CompletionException(new RequestException(404, Json.toJson("User not found")));
            } catch (Exception ex) {
                throw new CompletionException(new RequestException(400, Json.toJson("USer not found!!")));
            }
        }, ec.current());
    }


    public CompletableFuture<List<Dashboard>> hierarchy() {
        return CompletableFuture.supplyAsync(() -> {
            List<Dashboard> dashboards = mongoDB
                    .getMongoDatabase()
                    .getCollection("dashboard", Dashboard.class)
                    .aggregate(Arrays.asList(new Document("$match",
                                    new Document("parentId",
                                            new BsonNull())),
                            new Document("$graphLookup",
                                    new Document("from", "dashboard")
                                            .append("startWith", "$_id")
                                            .append("connectFromField", "_id")
                                            .append("connectToField", "parentId")
                                            .append("as", "children")
                                            .append("depthField", "level"))))
                    .into(new ArrayList<>());

//                List<Dashboard> children = dashboards.get(0).getChildren();

//            dashboards.forEach(x -> {
//                List<Dashboard> children = x.getChildren();
//                List<Dashboard> parentless = children
//                        .stream()
//                        .filter(y -> y.getLevel() == 0)
//                        .collect(Collectors.toList());
//                parentless.forEach(p -> recursivePath(x, children));
//
//                x.setChildren(parentless);
//
//            });

//            children.forEach(t -> {
//                if (t.getLevel() == 0){
//                    data.add(t);
//                } else {
//                    recursivePath(t, data);
//                }
//            });

            Dashboard parent;
            try {
                parent = dashboards.get(0).clone();
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }

            dashboards.forEach(next -> {
                recursivePath(parent, next.getChildren());
            });

            return List.of(parent);
        }, ec.current());
    }

//    public void recursivePath(Dashboard d1, List<Dashboard> d2) {
//
//            d2.forEach(dashboard -> {
//                if(d1.getId().equals(dashboard.getParentId())) {
//                    d1.getChildren().add(dashboard);
//                    recursivePath(dashboard,d2);
//                }
////            else {
////                recursivePath(d1, x.getChildren());
////            }
//            });
//
////        for (Dashboard dashboard : d2) {
////            if (d1.getId().equals(dashboard.getParentId())) {
////                d1.getChildren().add(dashboard);
////                recursivePath(dashboard, d2);
////            }
////        }
//    }

    public void recursivePath(Dashboard parent, List<Dashboard> child) {
        CompletableFuture.supplyAsync(() -> {
            for (Dashboard d : child) {
                if (parent.getId().equals(d.getParentId())) {
                    parent.getChildren().add(d);
                    recursivePath(d, child);
                }
            }
            return null;
        });
    }


}
