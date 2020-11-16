package botgen.api

import botgen.api.model.telegram.Update
import botgen.model._
import botgen.service.RequestHandler
import botgen.utils.HttpCodeException
import cats.syntax.applicative._
import cats.syntax.applicativeError._
import cats.syntax.either._
import cats.syntax.flatMap._
import cats.syntax.functor._
import cats.{ApplicativeError, MonadError}
import com.softwaremill.tagging._

class EndpointsImplementation[F[_]](requestHandler: RequestHandler[F])
                                   (implicit F: MonadError[F, Throwable]) {

  import EndpointsImplementation._

  def handleTelegramEvent(update: Update, token: BotToken): F[Either[ApiError, Unit]] =
    update.message match {
      case Some(message) =>
        val request = Request.ProcessMessage(
          botToken = token,
          message = Message(
            payload = Message.Payload(message.text.getOrElse(""), Seq.empty),
            chatId = message.chat.id.toString.taggedWith[Tags.ChatId]
          ))
        requestHandler.handle(request).transformErrors
      case None =>
        ().asRight[ApiError].pure[F]
    }

  def handleUpsertBot(token: BotToken,
                      botDefinition: BotDefinition): F[Either[ApiError, Unit]] =
    requestHandler
      .handle(Request.UpsertBot(token, botDefinition))
      .transformErrors

  def handleGetBot(token: BotToken): F[Either[ApiError, BotDefinition]] =
    requestHandler
      .handle(Request.GetBot(token))
      .flatMap {
        case Some(value) => value.pure[F]
        case None => new NoSuchElementException("No such bot").raiseError[F, BotDefinition]
      }
      .transformErrors
}

object EndpointsImplementation {

  private implicit class Ops[F[_], T](val f: F[T]) extends AnyVal {
    def transformErrors(implicit F: ApplicativeError[F, Throwable]): F[Either[ApiError, T]] =
      f.map(_.asRight[ApiError]).handleErrorWith {
        case e@(_: NoSuchElementException | HttpCodeException(404, _)) =>
          Either.left[ApiError, T](ApiError.NotFound(e.getMessage)).pure[F]
        case e =>
          Either.left[ApiError, T](ApiError.InternalServerError(e.getMessage)).pure[F]
      }
  }

}