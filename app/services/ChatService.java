package services;

import akka.actor.ActorSystem;
import akka.stream.Materializer;
import com.mongodb.client.MongoCollection;
import models.ChatRooom;
import models.enums.ChatTypes;
import mongo.IMongoDB;
import org.bson.types.ObjectId;
import play.libs.concurrent.HttpExecutionContext;

import javax.inject.Inject;
import java.net.http.WebSocket;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.mongodb.client.model.Filters.eq;
import static models.enums.ChatTypes.*;

public class ChatService {
//    public WebSocket chat(String room) {
//
//    }

    @Inject
    private ActorSystem actorSystem;

    @Inject
    private Materializer materializer;

    @Inject
    private UserService usService;

    @Inject
    private IMongoDB mongoDB;

    @com.google.inject.Inject
    HttpExecutionContext ec;

    public CompletableFuture<ChatTypes> accessType(ObjectId roomId, List<ObjectId> objectIds) {
        MongoCollection<ChatRooom> collection =  mongoDB.getMongoDatabase().getCollection("rooms", ChatRooom.class);
        ChatRooom room = collection.find(eq("id", roomId))
                .first();
        return CompletableFuture.supplyAsync(() -> {
            if(room == null ) {
                return ChatTypes.NOT_FOUND;
            }
            if(room.getReadACL().isEmpty() && room.getWriteACL().isEmpty()) {
                return WRITE;
            }

            boolean hasReadAccess = false;
            boolean hasWriteAccess = false;
            for (ObjectId id: objectIds) {
                if(room.getReadACL().contains(id)) {
                    hasReadAccess = true;
                }
                if(room.getWriteACL().contains(id)) {
                    hasWriteAccess = true;
                    break;
                }
            }
            if(hasWriteAccess) {
                return WRITE;
            }
            if(hasReadAccess) {
                return READ;
            }
            return NULL;
        }, ec.current());
    }
}
