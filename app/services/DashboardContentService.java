package services;

import com.google.inject.Inject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.InsertOneResult;
import exceptions.RequestException;
import models.Dashboard;
import models.User;
import mongo.IMongoDB;
import play.libs.Json;
import play.libs.concurrent.HttpExecutionContext;
import utils.ServiceUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class DashboardContentService {
    @Inject
    HttpExecutionContext ec;
    @Inject
    IMongoDB mongoDB;

    public CompletableFuture<Dashboard> save(User user, Dashboard dashboard) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                MongoCollection collection = mongoDB.getMongoDatabase()
                        .getCollection("Dashboard", Dashboard.class);
                dashboard.getReadACL().add(user.getId().toString());
                dashboard.getWriteACL().add(user.getId().toString());
                InsertOneResult toReturn = collection.insertOne(dashboard);
                if(!toReturn.wasAcknowledged() || toReturn.getInsertedId() == null) {
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
            try{
                return mongoDB.getMongoDatabase()
                        .getCollection("Dashboard", Dashboard.class)
                        .find(Filters.or(
                                Filters.in("readACL", user.getId().toString()),
                                Filters.in("readACL", ServiceUtils.getRoles(user)),
                                Filters.in("writeACL", user.getId().toString()),
                                Filters.in("writeACL", ServiceUtils.getRoles(user)),
                                Filters.and(
                                        Filters.eq("readACL", new ArrayList<String>()),
                                        Filters.eq("writeACL", new ArrayList<String>())
                                )
                        ))
                        .into(new ArrayList<>());
            }
            catch(Exception ex) {
                throw new CompletionException(new RequestException(400, Json.toJson("Something is wrong")));
            }
        }, ec.current());
    }

    public CompletableFuture<Dashboard> update(Dashboard dashboard) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                MongoCollection<Dashboard> collection = mongoDB.getMongoDatabase()
                        .getCollection("Dashboards", Dashboard.class);

                FindIterable<Dashboard> toReturn = collection.find(Filters.eq("_id", dashboard.getId()));
                Dashboard finalDashboard = toReturn.first();
                if(finalDashboard == null) {
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
            try{
                MongoCollection<Dashboard> collection = mongoDB.getMongoDatabase()
                        .getCollection("Dashboards", Dashboard.class);

                FindIterable<Dashboard> toReturn = collection.find(Filters.eq("_id", dashboard.getId()));
                Dashboard finalDashboard = toReturn.first();
                if(finalDashboard == null) {
                    throw new CompletionException(new RequestException(400, Json.toJson("Couldn't find!!!")));
                }
                collection.deleteOne(Filters.eq("_id", dashboard.getId()));
                return dashboard;

            }catch(NullPointerException | IllegalArgumentException ex) {
                throw new CompletionException(new RequestException(404, Json.toJson("User not found")));
            } catch(Exception ex) {
                throw new CompletionException(new RequestException(400, Json.toJson("USer not found!!")));
            }
        }, ec.current());
    }

}
