package services

import data.mariadb.MariaDB
//import data.mssql.MsSql
import data.phrasetranslation.TranslationResponse
import javax.inject.{Inject, Singleton}
import play.api.Configuration
import play.api.inject.ApplicationLifecycle
//import services.DHSServiceable
import utils.scaffolds.Logging
import play.api.libs.json.{JsArray, JsString, JsValue, Json}
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import org.apache.http.client.methods.HttpOptions
//import akka.actor.scaladsl.Behaviors
//import akka.http.scaladsl.Http
//import akka.http.scaladsl.model._

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.{ Failure, Success }
import scalaj.http.{Http, HttpOptions, HttpResponse}


@Singleton
class PhraseTranslationService @Inject()(lifecycle: ApplicationLifecycle, config: Configuration) extends Logging  {

  //val mariadb = MariaDB.Connection(config)

  implicit val system = ActorSystem()

  implicit val materializer = ActorMaterializer()

  import system.dispatcher

  val keyCreateUri=s"${config.getString("phraseapp.host")}${config.getString("phraseapp.project-id")}/keys?" +
    s"access_token=${config.getString("phraseapp.access-token")}"

  def submitTranslationrequest(translationRequest:String):TranslationResponse={
    val jsonRequest = Json.parse(translationRequest)
    val sponsorId = ((jsonRequest) \ ("sponsorId")).as[JsString].value
    val translations=(jsonRequest\("translations")).as[JsArray].value
    val serviceCode=(jsonRequest\("service")).as[JsString].value
    val locales=(jsonRequest\("locales")).as[JsArray].value

    var hashMapTranslations=scala.collection.mutable.Map[String, String]()
    val keyResFuture=Future.traverse(translations)(translation =>
      Future {
        createPhraseAppKey(translation, serviceCode)
      })

      keyResFuture
        .onComplete {
          case Success(keys) => for (key <- keys) println(s"key response ${key}");
            //val phraseTranslationIds=createPhraseAppTranslation(translation,extraxtKeyId(res))
          case Failure(_)   => sys.error("something wrong")
        }

      //hashMapTranslations.put(phraseKeyId, phraseTranslationId)

    //createPhraseAppJob(hashMapTranslations)
    new TranslationResponse
  }

  //def createPhraseAppTranslation(translation:JsValue, keyId:String)
  /*def createPhraseAppJob(keyIds:List[String])

  }*/
  //def addPhraseAppLocalestoJob()

  def httpPostRequest(uri:String, requestJsonStr:String):HttpResponse[String]={
    //def highlightCode(myCode: String): Future[String] = {
    val result = Http(uri).postData(requestJsonStr)//"""{"id":"12","json":"data"}""")
      .header("Content-Type", "application/json")
      .header("Charset", "UTF-8").asString
      //.option(HttpOptions.readTimeout(10000)).asString
   /* val responseFuture = Http().singleRequest(
      HttpRequest(
        method = HttpMethods.POST,
        uri = "http://markup.su/api/highlighter",
        entity = HttpEntity(ContentTypes.`application/json`,requestJsonStr)))
                  //s"source=${URLEncoder.encode(myCode.trim, "UTF-8")}&language=Scala&theme=Sunburst")))*/
    result
    /*responseFuture
        .flatMap(_.entity.toStrict(10 seconds))
        .map(_.data.utf8String)*/
    //}
  }

  def createPhraseAppKey(translation:JsValue, serviceCode:String):String={
   httpPostRequest(keyCreateUri,createKeyRequestJsonStr((translation\("key")).as[JsString].value,serviceCode)).body


  }

  def createKeyRequestJsonStr(key:String, tag:String):String={
    //val retJson=("name" -> Json.toJson(key)) + "tags" -> Json.toJson(tag)
    //retJson.toString().replace("\"","\\\"")
    s"""{"name":"${key}","tags":"${tag}"}"""

  }
}
