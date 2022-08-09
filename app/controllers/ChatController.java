package controllers;

import akka.actor.ActorSystem;
import akka.stream.Materializer;
import com.mongodb.client.MongoCollection;
import exceptions.RequestException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;
import models.ChatRooom;
import models.User;
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
import services.ChatService;
import services.UserService;
import utils.DatabaseUtils;
import utils.ServiceUtils;

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
    private ChatService chS;

    @Inject
    private IMongoDB mongoDB;

    @Inject
    private AuthenticationService authService;

    @com.google.inject.Inject
    HttpExecutionContext ec;


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


//    public WebSocket chat (String room, String token) {
//            return WebSocket.Text.acceptOrResult(request -> {
//
//            });
//        }






}



























