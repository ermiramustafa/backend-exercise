package actions;

//import services.UserService;
//
//import javax.inject.Inject;
//import java.util.concurrent.CompletableFuture;
//import java.util.concurrent.CompletionStage;
//
//public class AuthorizationAction extends Action.Simple{
//    @Inject
//    JwtValidator jwtValidator;
//    @Inject
//    UserService userService;
//
//    public static class Attributes {
//        public static final TypedKey<User> USER = TypedKey.create("user");
//    }
//
////    public CompletionStage<Result> call(Http.Request request) {
////        ObjectNode objectNode = jwtValidator.validateJwt(request);
////        if(!objectNode.has("userId")) {
////            return CompletableFuture.completedFuture(unauthorized(objectNode));
////        }
////        return userService.findUserById(objectNode.get("userId").asText())
////                .thenCompose(user -> delegate.call(request.addAttr(Attributes.USER, user)));
////    }
//}
