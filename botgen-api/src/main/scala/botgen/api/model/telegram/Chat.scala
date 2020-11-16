package botgen.api.model.telegram

import io.circe.Codec
import io.circe.generic.extras._
import io.circe.generic.semiauto.deriveCodec

@ConfiguredJsonCodec
case class Chat(id: Int)

object Chat {
  implicit val circeConfig: Configuration = Configuration.default.withSnakeCaseMemberNames
  implicit val codec: Codec[Chat] = deriveCodec
}