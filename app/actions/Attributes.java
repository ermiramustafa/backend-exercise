package actions;

import play.libs.typedmap.TypedKey;

/**
 * Created by Agon on 09/08/2020
 */
public class Attributes {
    public static final TypedKey TYPED_KEY = TypedKey.create("user");
    public static final TypedKey<String> AUTHENTICATION_TYPED_KEY = TypedKey.create("token");
}
