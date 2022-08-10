package actions;

import com.auth0.jwt.exceptions.JWTCreationException;
import com.google.inject.Inject;
import com.typesafe.config.Config;
import exceptions.RequestException;
import models.User;
import mongo.IMongoDB;
import utils.ServiceUtils;
import play.libs.Json;
import play.mvc.*;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;

public class AuthenticatedAction extends Action<Authenticated> {
    @Inject
    IMongoDB mongoDB;

    @Inject
    Config config;

    @Override
    public CompletionStage<Result> call(Http.Request request) {
        try {
            /*String token = ServiceUtils.getTokenFrom(request);
            byte[] decodedBytes = Base64.getDecoder().decode(token.split("\\.")[1]);
            String decodedToken = new String(decodedBytes);
            String id = Json.parse(decodedToken).get("iss").asText();

            MongoCollection<User> collection = mongoDB.getMongoDatabase()
                    .getCollection("users", User.class);

            User user = collection.find(Filters.eq("_id", new ObjectId(id))).first();
            if (user == null) {
                throw new CompletionException(new RequestException(Http.Status.NOT_FOUND, Json.toJson("User doesn't exist!")));
            }

            String secret = config.getString("play.http.secret.key");
            Algorithm algorithm = Algorithm.HMAC256(secret);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(user.getId().toString())
                    .build();
            verifier.verify(token);

            request = request.addAttr(Attributes.USER_TYPED_KEY, user);
            return delegate.call(request);*/
            String token = ServiceUtils.getTokenFromRequest(request);
            User user = ServiceUtils
                    .decodeToken(token)
                    .thenCompose(x->ServiceUtils.getUserFromId(mongoDB,x))
                    .thenCompose(x -> ServiceUtils.verify(config, x,token))
                    .join();

            request = request.addAttr(Attributes.USER_TYPED_KEY, user);
            return delegate.call(request);
        } catch (JWTCreationException ex) {
            ex.printStackTrace();
            throw new CompletionException(new RequestException(Http.Status.BAD_REQUEST, Json.toJson("Invalid Signing configuration / Couldn't convert Claims.")));
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new CompletionException(new RequestException(Http.Status.UNAUTHORIZED, Json.toJson("You are not authorized! Catch Related")));
        }
    }
}