package utils.helpers

import java.text.DecimalFormat
import java.time.format.DateTimeFormatter
import java.util.TimeZone

/**
  * This provides some reasonable formats.
  */
object Formatting {

  val decimalFormat: DecimalFormat = new DecimalFormat("#.00")

  /**
    * Local Date/Time Format. We use UTC for almost everything.
    */
  val dateFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy").withZone(TimeZone.getTimeZone("UTC").toZoneId)

  /**
    * This is important when we provide expirations in an HTTP Response header.
    */
  val headerDateFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss zzz").withZone(TimeZone.getTimeZone("UTC").toZoneId)
}
