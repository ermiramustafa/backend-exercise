package modules;

import com.google.inject.AbstractModule;
import actors.ConfiguredActor;
import play.libs.akka.AkkaGuiceSupport;

public class ActorModule extends AbstractModule implements AkkaGuiceSupport {

    @Override
    protected void configure() {
        bindActor(ConfiguredActor.class, "configured-actor");
//        bindActor(ParentActor.class, "parent-actor");
//        bindActorFactory(ConfiguredChildActor.class, ConfiguredChildActorProtocol.Factory.class);
    }
}
