package botgen.dao

import botgen.serialization.StringCodec
import io.circe.Codec

sealed trait Schema[T]

object Schema {
  case class Json[T](codec: Codec[T]) extends Schema[T]

  case class String[T](codec: StringCodec[T]) extends Schema[T]
}
