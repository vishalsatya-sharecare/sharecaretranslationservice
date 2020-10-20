package utils.implicits

import java.time.{LocalDateTime, ZoneId}

import play.api.http.HeaderNames
import play.api.mvc.Result
import utils.helpers.Formatting

/**
  * This provides an implicit class to add headers to a given Result for any endpoint.
  * Primarily used to provide caching for cumbersome responses.
  */
object ResultImplicits extends HeaderNames {

  val expirationHours  : Int = 2
  val expirationSeconds: Int = expirationHours * 60 * 60

  implicit class ResultImplicits(r: Result) {
    def disableCacheHeaders: Result = r.withHeaders(
      CACHE_CONTROL -> "no-cache"
    )
    def withCacheHeaders: Result = r.withHeaders(
      CACHE_CONTROL -> s"max-age=$expirationSeconds, private",
      EXPIRES -> LocalDateTime.now(ZoneId.of("UTC"))
        .plusHours(expirationHours).format(Formatting.headerDateFormat)
    )
  }

}


