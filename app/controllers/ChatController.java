package controllers;

import akka.actor.ActorSystem;
import akka.stream.Materializer;
import com.mongodb.client.MongoCollection;
import exceptions.RequestException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;
import models.ChatRooom;
import models.enums.ChatTypes;
import mongo.IMongoDB;
import org.bson.types.ObjectId;
import play.libs.F;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.WebSocket;
import services.AuthenticationService;
import services.UserService;
import utils.DatabaseUtils;

import javax.inject.Inject;
import javax.xml.bind.DatatypeConverter;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static com.mongodb.client.model.Filters.eq;
import static models.enums.ChatTypes.*;


public class ChatController extends Controller {

    @Inject
    private ActorSystem actorSystem;

    @Inject
    private Materializer materializer;

    @Inject
    private UserService usService;

    @Inject
    private IMongoDB mongoDB;

    @Inject
    private AuthenticationService authService;

    @com.google.inject.Inject
    HttpExecutionContext ec;




    public WebSocket chat(String room) {
        //User us =  //get user with token
        return WebSocket.Text.acceptOrResult(request -> getToken(request)
                .thenCompose(username -> usService.getUserACL(username))
                .thenCompose(roles -> typeOfAccess(new ObjectId(room), roles))
                .handle((res, e) -> {
                    if(e != null) {
                        Result result = DatabaseUtils.throwableToResult(e);
                        return F.Either.Left(result);
                    }
                    String username = parseToken(request.getHeaders().get("token").get());

                    return null;
                }));
    }




    public CompletableFuture<ChatTypes> typeOfAccess(ObjectId roomId, List<ObjectId> objectIds) {
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


    public String parseToken(String jwt) {
        try{
            return Jwts.parser()
                    .setSigningKey(DatatypeConverter.parseBase64Binary("encryption.private_key"))
                    .parseClaimsJws(jwt)
                    .getBody().get("content", String.class);
        } catch (SignatureException | ExpiredJwtException ex) {
            throw ex;
        }

    }

    public CompletableFuture<String> getToken(Http.RequestHeader request) {
        return CompletableFuture.supplyAsync(() -> {
            try{
                String token = request.getHeaders().get("token").get();
                return parseToken(token);
            } catch (NoSuchElementException | SignatureException | ExpiredJwtException ex) {
                throw new CompletionException(new RequestException(FORBIDDEN, ex.getMessage()));
            }
        }, ec.current());
    }

}



























