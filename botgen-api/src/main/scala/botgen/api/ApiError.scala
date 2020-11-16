package botgen.api

sealed trait ApiError {
  def message: String
}

object ApiError {
  case class InternalServerError(message: String) extends ApiError

  case class NotFound(message: String) extends ApiError
}
