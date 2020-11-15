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

  private val chatIdBotKeyRegex = "([a-zA-Z0-9]+)_(\\S+)".r

  implicit val chatIdBotKeyCodec: StringCodec[(ChatId, BotKey)] =
    StringCodec.from(
      encoder = {
        case (chatId, botKey) => s"${chatId}_$botKey"
      },
      decoder = {
        case chatIdBotKeyRegex(chatId, botKey) =>
          Right(chatId.taggedWith[Tags.ChatId] -> botKey.taggedWith[Tags.BotKey])
        case _ => Left(s"String should match $chatIdBotKeyRegex")
      }
    )
}
