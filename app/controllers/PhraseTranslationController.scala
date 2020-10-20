package controllers

import com.sharecare.sdk.sso.play.Auth
//import data.mssql.MsSqlService
import javax.inject._
import play.api.Configuration
import play.api.libs.ws.WSClient
import play.api.mvc._
import utils.implicits.ResultImplicits._
import utils.scaffolds.Logging

import scala.concurrent.ExecutionContext
import play.api.libs.json.JsArray
import services.PhraseTranslationService

import scala.collection.mutable


/**
  * This is a simple example controller for the scala play cloud template. @Inject provides dependency
  * injection for a handful of features:
  *
  * @param config configuration handling dependency injection (takes from /conf/)
  * @param ws     outgoing web request client
  * @param auth   authentication helper
  * @param exec   execution context for multi-threading and async processes.
  */
@Singleton
case class PhraseTranslationController @Inject()(
   config: Configuration
   , ws: WSClient
   , auth: Auth
   , ptservice: PhraseTranslationService)(implicit exec: ExecutionContext) extends ControllerFrame with Logging {

  val notProperTranslateRequest = new Exception("Phrase translation Request Json is invalid")

  def translatePhrases:Action[AnyContent] = sponsorAware() { req =>
    val translationRequestJson = (req.body.asJson.getOrElse(throw notProperTranslateRequest)).toString()
    ptservice.submitTranslationrequest(translationRequestJson)
    Ok
  }
  override val ControllerAddress: String = "phraseTranslator"
}