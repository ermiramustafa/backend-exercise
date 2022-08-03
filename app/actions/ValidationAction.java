package actions;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Strings;
import models.validators.HibernateValidator;
import play.libs.Json;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class ValidationAction extends Action<Validation> {
    @Override
    public CompletionStage<Result> call(Http.Request request) {
        try{
            JsonNode body = request.body().asJson();
            Object object = Json.fromJson(body, configuration.type());

            String errors = HibernateValidator.validate(object);
            if(!Strings.isNullOrEmpty(errors)) {
                return CompletableFuture.completedFuture(badRequest(Json.toJson(errors)));
            }
            return delegate.call(request);
        }
        catch(Exception ex) {
            ex.printStackTrace();
            ObjectNode response = Json.newObject();
            response.put("message", "Invalid object supplied, cannot cast to type Dashboard");
            return CompletableFuture.completedFuture(badRequest(response));
        }
    }
}