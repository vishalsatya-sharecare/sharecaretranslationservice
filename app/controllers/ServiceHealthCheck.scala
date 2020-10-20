package controllers

import akka.actor.ActorSystem
import com.sharecare.lib.play.healthcheck.controllers.HealthCheckController
import com.sharecare.lib.play.phr.controllers.TemplateHealthCheck
import javax.inject.{Inject, Named, Singleton}
import play.api.Configuration

import scala.concurrent.ExecutionContext

/**
  * This controller provides healthcheck information integral to the automated management provided by Maestro. This is
  * mapped to /healthcheck and allows for two parameters (?livelinessProbe and ?readinessProbe).
  *
  * @param config     config management package
  * @param hcc        in-house healthcheck controller
  * @param akkaSystem this makes it possible for this controller to live on its own thread and therefore remain
  *                   unimpeded by the processes of another controller.
  */
@Singleton
class ServiceHealthCheck @Inject()(
  config: Configuration
  , hcc : HealthCheckController
  , akkaSystem: ActorSystem) extends TemplateHealthCheck(Set.empty, config, hcc) {
  implicit val myExecutionContext: ExecutionContext = akkaSystem.dispatchers.lookup("akka.actor.healthcheck-context")
}

