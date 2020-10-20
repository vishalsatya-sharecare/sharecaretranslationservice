import com.sharecare.lib.play.errors.Error
import javax.inject.Singleton
import play.api.http.HttpErrorHandler
import play.api.libs.json.Json
import play.api.mvc.Results._
import play.api.mvc._
import utils.scaffolds.Logging

import scala.concurrent._

@Singleton
class ErrorHandler extends HttpErrorHandler with Logging {
  import com.sharecare.lib.play.errors.serialization.ErrorJsonFormats._

  def onClientError(request: RequestHeader, statusCode: Int, message: String): Future[Result] = {
    logger.debug(s"($statusCode RESPONSE SENT): \n\tREQUEST: ( $request )\n\tMES  SAGE: ( $message )")
    Future.successful(
      statusCode match {
        case 400 => BadRequest(Json.toJson(Error(message, Some(statusCode))))
        case 401 => Unauthorized(Json.toJson(Error(message, Some(statusCode))))
        case 404 => NotFound(Json.toJson(Error(message, Some(statusCode))))
        case _   => InternalServerError(Json.toJson(Error(message, Some(statusCode))))
      }
    )
  }

  def onServerError(request: RequestHeader, exception: Throwable): Future[Result] = {
    logger.error(s"(500 RESPONSE SENT): \n\tREQUEST: ( $request )\n\tMESSAGE: ( ${exception.getMessage} )")
    Future.successful(
      InternalServerError(Json.toJson(Error("A server error occurred: " + exception.getMessage, Some(500))))
    )
  }
}