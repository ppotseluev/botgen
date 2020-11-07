package botgen.serialization

trait StringDecoder[T] {
  def read(str: String): Either[String, T]
}
