package services;


import com.google.inject.Inject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.GraphLookupOptions;
import com.mongodb.client.result.InsertOneResult;
import exceptions.RequestException;
import models.BaseModel;
import models.Dashboard;
import models.User;
import models.codecs.Content;
import mongo.IMongoDB;
import org.bson.BsonNull;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import play.libs.Json;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Http;
import scala.Int;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Filters.eq;

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

    public CompletableFuture<List<Dashboard>> all(User user, int limit, int skip) {
        return CompletableFuture.supplyAsync(() -> {
            try {
//                int limit = 100, skip = 0;
                MongoCollection<Dashboard> collection = mongoDB
                        .getMongoDatabase()
                        .getCollection("dashboard", Dashboard.class);
                List<Dashboard> dashboards = collection.find(Filters.or(
                                        Filters.in("readACL", user.getId()),
                                        Filters.in("readACL", user.getRoles()),
                                        Filters.in("writeACL", user.getId()),
                                        Filters.in("writeACL", user.getRoles()),
                                        Filters.and(
                                                eq("readACL", new ArrayList<>()),
                                                eq("writeACL", new ArrayList<>()))
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
                                                eq("readACL", new ArrayList<>()),
                                                eq("writeACL", new ArrayList<>()))

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
                        .getCollection("dashboard", Dashboard.class);

                FindIterable<Dashboard> toReturn = collection.find(eq("_id", dashboard.getId()));
                Dashboard finalDashboard = toReturn.first();
                if (finalDashboard == null) {
                    throw new CompletionException(new RequestException(400, Json.toJson("Couldn't insert user")));
                }
                dashboard.getReadACL().addAll(finalDashboard.getReadACL());
                dashboard.getWriteACL().addAll(finalDashboard.getWriteACL());
                collection.replaceOne(eq("_id", dashboard.getId()), dashboard);
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
                        .getCollection("dashboard", Dashboard.class);

                FindIterable<Dashboard> toReturn = collection.find(eq("_id", dashboard.getId()));
                Dashboard finalDashboard = toReturn.first();
                if (finalDashboard == null) {
                    throw new CompletionException(new RequestException(400, Json.toJson("Couldn't insert user")));
                }
                collection.deleteOne(eq("_id", dashboard.getId()));
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
            MongoCollection<Dashboard> collection = mongoDB.getMongoDatabase()
                    .getCollection("dashboard", Dashboard.class);

            List<Bson> pipeline = new ArrayList<>();
            pipeline.add(Aggregates.match(eq("parentId", new BsonNull())));
            pipeline.add(Aggregates
                    .graphLookup("dashboard"
                            , "$_id"
                            , "_id"
                            , "parentId"
                            , "children"
                            , new GraphLookupOptions().depthField("level")));

            List<Dashboard> dashboards = collection
                    .aggregate(pipeline, Dashboard.class)
                    .into(new ArrayList<>());

            //trying to add content
            List<Dashboard> flatOption = dashboards.stream().map(Dashboard::getChildren)
                    .flatMap(Collection::stream).collect(Collectors.toList());
            flatOption.addAll(dashboards);
            List<ObjectId> objid = flatOption.stream().map(BaseModel::getId).collect(Collectors.toList());

            List<Content> contents = mongoDB.getMongoDatabase()
                    .getCollection("content", Content.class)
                    .find()
                    .filter(Filters.in("dashboardId" , objid))
                    .into(new ArrayList<>());
            flatOption.forEach(dash -> {
                dash.setContent(contents.stream().filter(x -> x.getDashboardId().equals(dash.getId())).collect(Collectors.toList()));
            });
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
            dashboards.forEach(dash -> {
                List<Dashboard> list = dash.getChildren();
                List<Dashboard> parentless = list.stream().filter(x -> x.getLevel() == 0).collect(Collectors.toList());
                parentless.forEach(x -> recursivePath(x, list));
                dash.setChildren(parentless);
            });

            return dashboards;

//            Dashboard parent;
//            try {
//                parent = dashboards.get(0).clone();
//            } catch (CloneNotSupportedException e) {
//                throw new RuntimeException(e);
//            }
//
//            dashboards.forEach(next -> {
//                recursivePath(parent, next.getChildren());
//            });
//
//            return List.of(parent);
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
