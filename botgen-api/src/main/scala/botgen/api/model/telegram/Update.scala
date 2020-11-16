package botgen.api.model.telegram

import io.circe.Codec
import io.circe.generic.extras._
import io.circe.generic.semiauto.deriveCodec

@ConfiguredJsonCodec
case class Update(updateId: Int,
                  message: Option[Message])

object Update {
  implicit val circeConfig: Configuration = Configuration.default.withSnakeCaseMemberNames
  implicit val codec: Codec[Update] = deriveCodec
}
