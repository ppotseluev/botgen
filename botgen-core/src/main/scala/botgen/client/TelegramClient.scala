package botgen.client

import botgen.client.TelegramClient.MessageSource
import botgen.model.BotToken
import io.circe.Codec
import io.circe.generic.extras.{Configuration, ConfiguredJsonCodec}
import io.circe.generic.semiauto.deriveCodec

trait TelegramClient[F[_]] {
  def send(botToken: BotToken)(messageSource: MessageSource): F[Unit]
}

object TelegramClient {

  @ConfiguredJsonCodec
  case class MessageSource(chatId: String,
                           text: String)

  object MessageSource {
    implicit val circeConfig: Configuration = Configuration.default.withSnakeCaseMemberNames
    implicit val messageSourceCodec: Codec[MessageSource] = deriveCodec
  }

}