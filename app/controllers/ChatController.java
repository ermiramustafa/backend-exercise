package controllers;

import actors.ChatActor;
import akka.actor.ActorSystem;
import akka.stream.Materializer;
import models.User;
import play.libs.streams.ActorFlow;
import play.mvc.Controller;
import play.mvc.WebSocket;
import services.UserService;
import utils.ServiceUtils;

import javax.inject.Inject;

public class ChatController extends Controller {

    @Inject
    private ActorSystem actorSystem;

    @Inject
    private Materializer materializer;

    @Inject
    private UserService usService;

    public WebSocket chat(String room, String token) {
        //User us =  //get user with token
        return WebSocket.Text.accept(request -> ActorFlow.actorRef((out) -> ChatActor.props(out, room), actorSystem, materializer));
    }
}
