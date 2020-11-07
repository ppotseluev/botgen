package botgen.client.impl

import botgen.client.TelegramClient
import botgen.model.BotToken
import botgen.utils.HttpClientUtils._
import cats.MonadError
import cats.syntax.functor._
import sttp.client._
import sttp.client.circe._
import sttp.model.{Header, MediaType}

class HttpTelegramClient[F[_]](telegramAddress: String)
                              (implicit sttpBackend: SttpBackend[F, Nothing, NothingT],
                               F: MonadError[F, Throwable])
  extends TelegramClient[F] {

  override def send(botToken: BotToken)
                   (messageSource: TelegramClient.MessageSource): F[Unit] =
    basicRequest
      .post(uri"https://$telegramAddress/bot$botToken/sendMessage")
      .header(Header.contentType(MediaType.ApplicationJson))
      .body(messageSource)
      .send()
      .checkStatusCode()
      .void
}
