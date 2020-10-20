package utils.implicits

import com.sharecare.lib.play.errors.CommonErrors
import play.api.libs.json.Json
import play.api.mvc.Result
import play.api.mvc.Results.InternalServerError

import scala.concurrent.Future
import scala.util.control.NonFatal

/**
  * Recovers from futures in order to extract and neatly handle unforeseen exceptions.
  *
  * If something goes wrong and you get a questionable 500 - try adding a breakpoint here.
  */
object FutureResultRecoveryImplicits {

  import com.sharecare.lib.play.errors.serialization.ErrorJsonFormats._

  import scala.concurrent.ExecutionContext.Implicits.global

  implicit class RecoverImplicits(f: Future[Result]) {
    def recoverWithInternatServerErrorResult: Future[Result] = f.recover {
      case NonFatal(e) => InternalServerError(Json.toJson(CommonErrors.InternalServerError))
    }
  }

}
