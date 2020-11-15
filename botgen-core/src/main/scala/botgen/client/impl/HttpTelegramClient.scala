package botgen.client.impl

import botgen.client.TelegramClient
import botgen.model.BotToken
import botgen.utils.HttpClientUtils._
import cats.MonadError
import cats.syntax.functor._
import io.circe.Printer
import io.circe.syntax._
import sttp.client._
import sttp.model.{Header, MediaType}

class HttpTelegramClient[F[_]](telegramUrl: String)
                              (implicit sttpBackend: SttpBackend[F, Nothing, NothingT],
                               F: MonadError[F, Throwable])
  extends TelegramClient[F] {

  override def send(botToken: BotToken)
                   (messageSource: TelegramClient.MessageSource): F[Unit] = {
    val json = Printer.noSpaces
      .copy(dropNullValues = true)
      .print(messageSource.asJson)
    basicRequest
      .post(uri"$telegramUrl/bot$botToken/sendMessage")
      .header(Header.contentType(MediaType.ApplicationJson))
      .body(json)
      .send()
      .checkStatusCode()
      .void
  }

  override def setWebhook(botToken: BotToken, url: String): F[Unit] =
    basicRequest
      .get(uri"$telegramUrl/bot$botToken/setWebhook?url=$url")
      .send()
      .checkStatusCode()
      .void
}
