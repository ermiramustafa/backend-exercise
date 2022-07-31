package actions;

import com.google.inject.Inject;
import exceptions.RequestException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;
import services.AuthenticationService;
import utils.DatabaseUtils;

import java.util.NoSuchElementException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;

import static play.mvc.Http.Status.FORBIDDEN;

public class AuthenticatedAction extends Action<Authenticated> {

    @Inject
    AuthenticationService authService;

    @Override
    public CompletionStage<Result> call(Http.Request request) {
        try {
            String token = request.getHeaders().get("token").get();

            String username = authService.parseToken(token);

            request = request.addAttr(Attributes.AUTHENTICATION_TYPED_KEY, username);
            return delegate.call(request);
        } catch (NoSuchElementException | SignatureException | ExpiredJwtException ex) {
            return CompletableFuture.completedFuture(DatabaseUtils.throwableToResult(new CompletionException(new RequestException(FORBIDDEN, ex.getMessage()))));
        }
    }
}
