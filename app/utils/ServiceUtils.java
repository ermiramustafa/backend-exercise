package utils;

import models.Role;
import models.User;
import play.mvc.Http;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ServiceUtils {
    public static String getTokenFrom (Http.Request request) {
        Optional<String> optionalToken = request.getHeaders().get("token");
        return optionalToken.orElse(null);
    }

    public static List<String> getRoles(User user) {
        return user.getRoles()
                .stream()
                .map(Role::getName)
                .collect(Collectors.toList());
    }

}