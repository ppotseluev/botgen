package botgen.serialization

trait StringEncoder[T] {
  def write(obj: T): String
}
