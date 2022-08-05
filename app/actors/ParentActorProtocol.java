package actors;

import lombok.Data;

public class ParentActorProtocol {

    @Data
    public static class GetChild {
        private String key;
    }
}
