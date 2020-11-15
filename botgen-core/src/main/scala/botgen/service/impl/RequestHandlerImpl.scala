package botgen.service.impl

import botgen.bot.{BotInput, BotLogic}
import botgen.compiler.BotCompiler
import botgen.dao.BotDefinitionDao
import botgen.model.{BotKey, BotToken, Request, Tags}
import botgen.service.{ChatService, RequestHandler}
import cats.Monad
import cats.syntax.flatMap._
import cats.syntax.functor._

class RequestHandlerImpl[F[_] : Monad](botLogic: BotLogic,
                                       botCompiler: BotCompiler[F],
                                       botDefinitionDao: BotDefinitionDao[F],
                                       chatService: ChatService[F],
                                       webhookUrl: BotToken => String,
                                       toKey: BotToken => BotKey)
  extends RequestHandler[F] {

  override def handle[T](request: Request[T]): F[T] = request match {
    case Request.ProcessMessage(botToken, message) =>
      val botInput = BotInput(botToken, message)
      botLogic(botInput).foldMap(botCompiler)
    case Request.UpsertBot(botToken, botDefinition) =>
      for {
        _ <- chatService.setWebhook(botToken, webhookUrl(botToken))
        _ <- botDefinitionDao.put(toKey(botToken), botDefinition)
      } yield ()
    case Request.GetBot(botToken) =>
      botDefinitionDao.get(toKey(botToken))
  }
}
