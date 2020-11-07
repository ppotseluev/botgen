package botgen.compiler

import botgen.bot.{BotDsl, BotError}
import botgen.client.TelegramClient
import botgen.dao.{BotScenarioDao, BotStateDao}
import botgen.model.{BotInfo, Message}
import botgen.service.ChatService
import cats.syntax.applicativeError._
import cats.syntax.functor._
import cats.{ApplicativeError, ~>}

class BotCompiler[F[_]](botScenarioDao: BotScenarioDao[F],
                        botStateDao: BotStateDao[F],
                        chatService: ChatService[F])
                       (implicit F: ApplicativeError[F, Throwable]) {
  def transformation: BotDsl ~> F = new (BotDsl ~> F) {
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
}
