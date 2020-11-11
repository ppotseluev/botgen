package botgen.serialization

import botgen.model.{BotKey, ChatId, Tags}
import cats.syntax.invariant._
import com.softwaremill.tagging._

object StringCodecInstances {
  implicit val transparentStringCodec: StringCodec[String] =
    StringCodec.from(
      decoder = Right.apply,
      encoder = identity
    )

  implicit val botKeyStringCodec: StringCodec[BotKey] =
    StringCodec[String].imap(_.taggedWith[Tags.BotKey])(identity)

  implicit val chatIdStringCodec: StringCodec[ChatId] =
    StringCodec[String].imap(_.taggedWith[Tags.ChatId])(identity)
}
