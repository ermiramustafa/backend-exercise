package services;

import com.google.inject.Inject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import com.mongodb.client.model.ReturnDocument;
import com.mongodb.client.result.DeleteResult;
import executors.MongoExecutionContext;
import mongo.IMongoDB;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.mongodb.client.model.Filters.eq;

public class DBservice {

    @Inject
    IMongoDB mongoDB;

    @Inject
    MongoExecutionContext mEC;

    public <T> CompletableFuture<List<T>> all(Class<T> type, List<Bson> pipeline, String collectionName) {
        return CompletableFuture.supplyAsync(() -> {
            MongoCollection<T> collection = mongoDB.getMongoDatabase()
                    .getCollection(collectionName, type);

            return collection
                    .aggregate(pipeline, type)
                    .into(new ArrayList<>());
        }, mEC);
    }

    public <T> CompletableFuture<List<T>> all(Class<T> type, Bson filter, String collectionName) {
        return CompletableFuture.supplyAsync(() -> {
            MongoCollection<T> collection = mongoDB.getMongoDatabase()
                    .getCollection(collectionName, type);

            return collection
                    .find(filter)
                    .into(new ArrayList<>());
        }, mEC);
    }

    public <T> CompletableFuture<ObjectId> save(Class<T> type, T item, String collectionName) {
        return CompletableFuture.supplyAsync(() -> {
            MongoCollection<T> collection = mongoDB.getMongoDatabase()
                    .getCollection(collectionName, type);

            return collection.insertOne(item).getInsertedId().asObjectId().getValue();
        }, mEC);
    }

    public <T> CompletableFuture<T> update(Class<T> type, T item, Bson filter, String collectionName) {
        return CompletableFuture.supplyAsync(() -> {
            MongoCollection<T> collection = mongoDB.getMongoDatabase()
                    .getCollection(collectionName, type);

            FindOneAndReplaceOptions options = new FindOneAndReplaceOptions().returnDocument(ReturnDocument.AFTER);

            T result = collection.findOneAndReplace(filter, item, options);
            return result;
        }, mEC);
    }

    public <T> CompletableFuture<Long> delete(Class<T> type, Bson filter, String collectionName) {
        return CompletableFuture.supplyAsync(() -> {
            MongoCollection<T> collection = mongoDB.getMongoDatabase()
                    .getCollection(collectionName, type);

            DeleteResult result = collection.deleteOne(filter);
            return result.getDeletedCount();
        }, mEC);
    }

    public <T> CompletableFuture<T> find(Class<T> type, String field, String value, String collectionName){
        return find(type, eq(field, value), collectionName);
    }

    public <T> CompletableFuture<T> find(Class<T> type, String field, ObjectId value, String collectionName){
        return find(type, eq(field, value), collectionName);
    }

    public <T> CompletableFuture<T> find(Class<T> type, Bson filter, String collectionName){
        return CompletableFuture.supplyAsync(() -> {
            MongoCollection<T> collection = mongoDB.getMongoDatabase()
                    .getCollection(collectionName, type);

            return collection.find(filter).first();
        }, mEC);
    }

}