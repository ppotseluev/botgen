package botgen.client

import botgen.client.TelegramClient.MessageSource
import botgen.model.BotToken
import io.circe.Codec
import io.circe.generic.extras.{Configuration, ConfiguredJsonCodec}
import io.circe.generic.semiauto.deriveCodec

trait TelegramClient[F[_]] {
  def send(botToken: BotToken)(messageSource: MessageSource): F[Unit]

  def setWebhook(botToken: BotToken, url: String): F[Unit]
}

object TelegramClient {
  implicit private val circeConfig: Configuration = Configuration.default.withSnakeCaseMemberNames

  @ConfiguredJsonCodec
  case class KeyboardButton(text: String)

  object KeyboardButton {
    implicit val keyboardButtonCodec: Codec[KeyboardButton] = deriveCodec
  }

  @ConfiguredJsonCodec
  case class ReplyMarkup(keyboard: Option[Seq[Seq[KeyboardButton]]] = None,
                         removeKeyboard: Option[Boolean] = None)

  object ReplyMarkup {
    implicit val keyboardCodec: Codec[ReplyMarkup] = deriveCodec
  }

  @ConfiguredJsonCodec
  case class MessageSource(chatId: String,
                           text: String,
                           replyMarkup: Option[ReplyMarkup])

  object MessageSource {
    implicit val messageSourceCodec: Codec[MessageSource] = deriveCodec
  }

}