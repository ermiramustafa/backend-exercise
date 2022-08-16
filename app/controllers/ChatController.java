package controllers;

import akka.actor.ActorSystem;
import akka.stream.Materializer;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.JsonNode;
import com.mongodb.client.MongoCollection;
import com.typesafe.config.Config;
import actions.Authenticated;
import actors.ChatActor;
import exceptions.RequestException;
import models.ChatRooom;
import models.User;
import mongo.IMongoDB;
import services.AuthenticationService;
//import io.exercise.api.services.ChatService;
import services.DashboardService;
import services.SerializationService;
import services.UserService;
import utils.ServiceUtils;
import org.bson.types.ObjectId;
import play.libs.F;
import play.libs.Json;
import play.libs.streams.ActorFlow;
import play.mvc.*;
import com.mongodb.client.model.Filters;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;


public class ChatController extends Controller {

    @Inject
    SerializationService serializationService;

    @Inject
    private ActorSystem actorSystem;
    @Inject
    private Materializer materializer;
    @Inject

    IMongoDB mongoDB;

    @Inject

    Config config;

    @Inject
    AuthenticationService auth;


    //    public WebSocket chat(Http.Request request,String roomId) {
//        //User us =  //get user with token
//        User user = ServiceUtils.getUserFrom(request);
//        return WebSocket.Text.accept((req) -> {
//            if(user == null) {
////                return F.Either.Left(forbidden("Not logged in"));
//                return CompletableFuture.completedFuture(F.Either.Left(forbidden("Please logIn if you want to be part of the chat!")));
//            }
//            return CompletableFuture.completedFuture(F.Either.Right(ActorFlow.actorRef((out) -> ChatActor.props(out, roomId), actorSystem, materializer)));
//
////            if()
//
//        });
//    }


//    public WebSocket chat(String room) {
//        //User us =  //get user with token
//        return WebSocket.Text.acceptOrResult(request -> authService.getToken(request)
//                .thenCompose(username -> usService.getUserACL(username))
//                .thenCompose(roles -> chS.accessType(new ObjectId(room), roles))
//                .handle((res, e) -> {
//                    if(e != null) {
//                        Result result = DatabaseUtils.throwableToResult(e);
//                        return F.Either.Left(result);
//                    }
//                    String username = authService.pToken(request.getHeaders().get("token").get());
//                    //todo roms
//                    return null;
//                }));
//    }


//        public WebSocket chat (String room, String token) {
//            return WebSocket.Text.acceptOrResult(request -> {
//                if(request.getHeaders().get(token).isEmpty()) {
//                    return CompletableFuture.completedFuture(F.Either.Left(badRequest("Token is missing")));
//                }
//
//            });
//        }

    public WebSocket chat(String roomId, String token) {
        return WebSocket.Text.acceptOrResult(request -> {
            User user = ServiceUtils
                    .decodeToken(token)
                    .thenCompose(x -> ServiceUtils.getUserFromId(mongoDB, x))
                    .thenCompose(x -> ServiceUtils.verify(config, x, token))
                    .join();

            System.out.println("User is : " + user);

            MongoCollection<ChatRooom> collection = mongoDB.getMongoDatabase()
                    .getCollection("rooms", ChatRooom.class);
            ChatRooom room = collection.find(Filters.eq("_id", new ObjectId(roomId)))
                    .first();

            if (room == null) {
                throw new CompletionException(new RequestException(Http.Status.NOT_FOUND, Json.toJson("Chat not found!")));
            }
            String userId = user.getId().toString();
            List<String> roles = user.getRoles();
            boolean read = false;
            boolean write = false;
            for (String id : roles) {
                if (room.getReadACL().contains(id) || room.getReadACL().contains(userId)) {
                    read = true;
                }
                if (room.getWriteACL().contains(id) || room.getWriteACL().contains(userId)) {
                    read = true;
                    write = true;
                }
            }
            if (!read) {
                //throw new CompletionException(new RequestException(Http.Status.FORBIDDEN, Json.toJson("")));
                return CompletableFuture.completedFuture(F.Either.Left(forbidden("You don't have access")));
            }
            boolean finalWrite = write;
            return CompletableFuture.completedFuture(F.Either.Right(ActorFlow.actorRef((out) -> ChatActor.props(out, roomId, finalWrite), actorSystem, materializer)));

        });
    }


}



























