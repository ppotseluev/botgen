package botgen.api.model.telegram

import io.circe.Codec
import io.circe.generic.extras._
import io.circe.generic.semiauto.deriveCodec

@ConfiguredJsonCodec
case class Message(messageId: Int,
                   from: Option[User],
                   chat: Chat,
                   text: Option[String])

object Message {
  implicit val circeConfig: Configuration = Configuration.default.withSnakeCaseMemberNames
  implicit val codec: Codec[Message] = deriveCodec
}
