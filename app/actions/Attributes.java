package actions;

import models.AuthenticationModel;
import models.User;
import play.libs.typedmap.TypedKey;

/**
 * Created by Agon on 09/08/2020
 */
public class Attributes {
    public static final TypedKey<User> USER_TYPED_KEY = TypedKey.<User>create("user");
}
