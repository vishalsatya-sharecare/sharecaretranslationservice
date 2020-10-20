package controllers

import com.iheart.playSwagger.SwaggerSpecGenerator
import javax.inject.Inject
import play.api.Configuration
import play.api.mvc.{Action, AnyContent, Controller, EssentialAction}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * This provides the documentation UI that other employees will use.
  *
  * @param conf configuration assistant -- allows access at conf/[instance].conf
  */
case class SwaggerController @Inject()(conf: Configuration) extends Controller {
  val appConfiguration: Option[Configuration] = conf.getConfig("application")
  val appRoute        : String                = appConfiguration.flatMap[String](_.getString("route"))
    .getOrElse(throw new Exception("You need to include app.route in your Application config (base.conf/reference.conf)."))

  implicit val cl = getClass.getClassLoader

  val DomainPackage    = "data"
  val PhrCommonPackage = "com.sharecare.lib.play.phr.models"
  val ErrorPackage     = "com.sharecare.lib.play.errors"

  private lazy val generator = SwaggerSpecGenerator(DomainPackage, PhrCommonPackage, ErrorPackage)

  def specs: EssentialAction = Action.async(Future.fromTry(generator.generate()).map(Ok(_)))
  def api: Action[AnyContent] = Action(Redirect(s"/$appRoute/docs"))
  def docs: Action[AnyContent] = Action(Redirect(s"/$appRoute/docs/index.html?url=/$appRoute/assets/swagger.json"))

}