package utils;

import play.mvc.Http;

import java.util.Optional;

public class ServiceUtils {
    public static String getTokenFrom (Http.Request request) {
        Optional<String> optionalToken = request.getHeaders().get("token");
        return optionalToken.orElse(null);
    }

}