package models

/**
  * Example case class to show how models can be passed around in controllers and how they're unit tested.
  *
  * @param id      example id
  * @param message example message
  */
case class Example(id: Int, message: String) {

  override def toString: String = s"$id:$message"
}
