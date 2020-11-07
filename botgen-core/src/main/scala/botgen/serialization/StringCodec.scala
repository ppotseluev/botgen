package botgen.serialization

trait StringCodec[T] extends StringDecoder[T] with StringEncoder[T]