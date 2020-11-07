package botgen.utils

case class HttpCodeException(code: Int,
                             message: String)
  extends RuntimeException
