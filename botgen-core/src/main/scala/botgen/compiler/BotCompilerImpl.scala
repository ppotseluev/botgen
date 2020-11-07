package botgen.compiler

import botgen.bot.{BotDsl, BotError}
import botgen.dao.{BotScenarioDao, BotStateDao}
import botgen.model.BotInfo
import botgen.service.ChatService
import cats.ApplicativeError
import cats.syntax.applicativeError._
import cats.syntax.functor._

class BotCompilerImpl[F[_]](botScenarioDao: BotScenarioDao[F],
                            botStateDao: BotStateDao[F],
                            chatService: ChatService[F])
                           (implicit F: ApplicativeError[F, Throwable])
  extends BotCompiler[F] {

  override def apply[A](botDsl: BotDsl[A]): F[A] = botDsl match {
    case BotDsl.LoadScenario(botKey) =>
      botScenarioDao.get(botKey).map(_.asInstanceOf[A])
    case BotDsl.GetCurrentState(chatId) =>
      botStateDao.get(chatId).map(_.asInstanceOf[A])
    case BotDsl.SaveState(chatId, botStateId) =>
      botStateDao.put(chatId, BotInfo(botStateId)).map(_.asInstanceOf[A])
    case BotDsl.Reply(botToken, chatId, message) =>
      chatService.send(botToken)(chatId)(message).map(_.asInstanceOf[A])
    case BotDsl.RaiseError(botError) =>
      botError match {
        case BotError.ScenarioNotFound =>
          //TODO improve error handling
          new NoSuchElementException("No such bot").raiseError
      }
  }
}
