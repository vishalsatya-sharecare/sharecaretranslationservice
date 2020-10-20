package controllers

import play.api.mvc.{Action, Controller, Result}
import utils.helpers.ApplicationConstants

import scala.concurrent.{Future}
import com.sharecare.sdk.sso.play.Auth.SponsorExtractionFunc
import com.sharecare.sdk.sso.play.{Auth, Defaults}
import play.api.mvc._

trait ControllerFrame extends Controller {

  val auth: Auth

  val ControllerAddress: String

  def sponsorAware[A](extractor: SponsorExtractionFunc[A] = Defaults.sponsorExtractor[A] _): ActionBuilder[Request] =
    preAuth //andThen auth.AuthorizeAccessNoConstraints[A](sponsorExtractor = extractor) andThen postAuth

  def preAuth = new ActionBuilder[Request] {
    override def invokeBlock[A](request: Request[A], block: Request[A] => Future[Result]): Future[Result] = {
      block(request)
    }
  }

  def postAuth = new ActionBuilder[Request] {
    override def invokeBlock[A](request: Request[A], block: Request[A] => Future[Result]): Future[Result] = {
      block(request)
    }
  }


  def genUri(route: String): String =
    s"/${Seq(ApplicationConstants.HostAddress, ControllerAddress, route) mkString "/"}"

  val implementMe = Action.async { _ =>
    Future.successful { NotImplemented }
  }

}
