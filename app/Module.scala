import java.time.Clock

import com.sharecare.sdk.sso.play.Auth
import com.google.inject.AbstractModule
//import com.sharecare.lib.play.security.authentication.mongoDB.UserLookup
//import com.sharecare.lib.play.security.authorization.AuthorizedFactory
//import data.mongo.MongoConnector

/**
  * This class is a Guice module that tells Guice how to bind several
  * different types. This Guice module is created when the Play
  * application starts.
  *
  * Play will automatically use any class called `Module` that is in
  * the root package. You can create modules in other locations by
  * adding `play.modules.enabled` settings to the `reference.conf`
  * configuration file.
  */
class Module extends AbstractModule {

  override def configure(): Unit = {
//    bind(classOf[AuthorizedFactory]).asEagerSingleton()
//    bind(classOf[UserLookup]).asEagerSingleton()
    bind(classOf[Auth]).asEagerSingleton()
    bind(classOf[Clock]).toInstance(Clock.systemDefaultZone)
//    bind(classOf[MongoConnector]).asEagerSingleton()
  }

}
