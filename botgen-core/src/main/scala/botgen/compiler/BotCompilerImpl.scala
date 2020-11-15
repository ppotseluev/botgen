package botgen.compiler

import botgen.bot.{BotDsl, BotError}
import botgen.dao.{BotDefinitionDao, BotStateDao}
import botgen.model.{BotInfo, BotKey, BotToken}
import botgen.service.ChatService
import cats.ApplicativeError
import cats.syntax.applicativeError._
import cats.syntax.functor._

class BotCompilerImpl[F[_]](botDefinitionDao: BotDefinitionDao[F],
                            botStateDao: BotStateDao[F],
                            chatService: ChatService[F],
                            toKey: BotToken => BotKey)
                           (implicit F: ApplicativeError[F, Throwable])
  extends BotCompiler[F] {

  override def apply[A](botDsl: BotDsl[A]): F[A] = botDsl match {
    case BotDsl.LoadScenario(botToken) =>
      botDefinitionDao.get(toKey(botToken)).map(_.map(_.scenario))
    case BotDsl.GetCurrentState(chatId) =>
      botStateDao.get(chatId).map(_.map(_.botStateId))
    case BotDsl.SaveState(chatId, botStateId) =>
      botStateDao.put(chatId, BotInfo(botStateId))
    case BotDsl.Reply(botToken, chatId, message) =>
      chatService.send(botToken)(chatId)(message)
    case BotDsl.RaiseError(botError) =>
      botError match {
        case BotError.ScenarioNotFound =>
          //TODO improve error handling
          new NoSuchElementException("No such bot").raiseError
      }
  }
}
