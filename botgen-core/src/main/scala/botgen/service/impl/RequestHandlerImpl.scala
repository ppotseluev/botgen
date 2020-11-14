package botgen.service.impl

import botgen.bot.{BotInput, BotLogic}
import botgen.client.TelegramClient
import botgen.compiler.BotCompiler
import botgen.dao.BotDefinitionDao
import botgen.model.{BotToken, Request, Tags}
import botgen.service.RequestHandler
import cats.Monad
import cats.syntax.flatMap._
import cats.syntax.functor._

class RequestHandlerImpl[F[_] : Monad](botLogic: BotLogic,
                                       botCompiler: BotCompiler[F],
                                       botDefinitionDao: BotDefinitionDao[F],
                                       telegramClient: TelegramClient[F],
                                       webhookUrl: BotToken => String)
  extends RequestHandler[F] {

  override def handle[T](request: Request[T]): F[T] = request match {
    case Request.ProcessMessage(botToken, message) =>
      val botInput = BotInput(botToken, message)
      botLogic(botInput).foldMap(botCompiler)
    case Request.UpsertBot(botToken, botDefinition) =>
      //TODO set webhook? only on creation?
      for {
        _ <- botDefinitionDao.put(botToken.taggedWith[Tags.BotKey], botDefinition) //TODO hash token
        _ <- telegramClient.setWebhook(botToken, webhookUrl(botToken))
      } yield ()
    case Request.GetBot(botKey) =>
      botDefinitionDao.get(botKey)
  }
}
