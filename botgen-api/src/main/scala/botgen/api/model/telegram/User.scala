package botgen.api.model.telegram

import io.circe.Codec
import io.circe.generic.extras._
import io.circe.generic.semiauto.deriveCodec

@ConfiguredJsonCodec
case class User(id: Int,
                firstName: String,
                lastName: Option[String],
                username: Option[String])

object User {
  implicit val circeConfig: Configuration = Configuration.default.withSnakeCaseMemberNames
  implicit val codec: Codec[User] = deriveCodec
}
