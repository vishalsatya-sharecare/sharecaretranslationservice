package utils.scaffolds

import org.slf4j.LoggerFactory
import play.api.{Logger => PlayLogger}

/**
  * Provides a simple scaffold that create a local logger for everything based on className.
  */
trait Logging {
  val logger: PlayLogger = new PlayLogger(LoggerFactory.getLogger(this.getClass.getSimpleName))
}
