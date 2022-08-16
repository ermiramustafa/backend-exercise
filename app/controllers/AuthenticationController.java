package controllers;

import com.google.inject.Inject;
import models.AuthenticationModel;
import play.mvc.*;
import services.AuthenticationService;
import services.SerializationService;
import services.UserService;
import utils.DatabaseUtils;

import java.util.concurrent.CompletableFuture;

public class AuthenticationController extends Controller {
    /*public Result authenticate(Http.Request request) {
        JsonNode node = request.body().asJson();
        User user = Json.fromJson(node, User.class);

        String secret = "asdfSFS34wfsdfsdfSDSD32dfsddDDerQSNCK34SOWEK5354fdgdf4";

        Key hmacKey = new SecretKeySpec(Base64.getDecoder().decode(secret),
                SignatureAlgorithm.HS256.getJcaName());

        String jwtToken = Jwts.builder()
                .claim("name", "Ermira Mustafa")
                .claim("email", "ermiramustafa@gmail.com")
                .setSubject("ermira")
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(Instant.now().plus(51, ChronoUnit.MINUTES)))
                .compact();

        return ok(views.html.index.render());
        //return ok(Json.toJson(jwtToken));
       // return jwtToken;
    }

    /*public Result authenticate(Http.Request request) {
        JsonNode node = request.body().asJson();
        User user = Json.fromJson(node, User.class);
        try {
            Algorithm algorithm = Algorithm.HMAC256("secret");
            String token = JWT.create()
                    .withIssuer("auth0")
                    .sign(algorithm);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return ok(views.html.index.render());
    }*/

    //    public Result authenticate(Http.Request request) {
//        JsonNode node =request.body().asJson();
//        try {
//            User user = new User("ermiramustafa", "password");
//            user.setId(new ObjectId());
//            String secret = "asdfSFS34wfsdfsdfSDSD32dfsddDDerQSNCK34SOWEK5354fdgdf4";
//            Algorithm algorithm = Algorithm.HMAC256("secret");
//            String token = JWT.create()
//                    .withClaim("id", String.valueOf(user.getId()))
//                    .sign(algorithm);
//
//        } catch (UnsupportedEncodingException e) {
//            throw new RuntimeException(e);
//        }
//
//        return ok(views.html.index.render());
//    }
    @Inject
    SerializationService service;

    @Inject
    AuthenticationService authService;

    @Inject
    UserService userService;

    @BodyParser.Of(BodyParser.Json.class)
    public CompletableFuture<Result> authenticate(Http.Request request) {
        return service.parseBodyOfType(request, AuthenticationModel.class)
                .thenCompose((data) -> authService.authenticate(data))
                .thenCompose((data) -> service.toJsonNode(data))
                .thenApply(Results::ok)
                .exceptionally(DatabaseUtils::throwableToResult);
    }

}
