package botgen.bot

import botgen.bot.scenario.GraphBotScenario
import botgen.model._
import cats.free.Free
import cats.free.Free.liftF

sealed trait BotDsl[T]

object BotDsl {
  /**
   * Free monad based bot's EDSL
   */
  type BotScript[T] = Free[BotDsl, T]

  case class LoadScenario(botToken: BotToken) extends BotDsl[Option[GraphBotScenario]]

  case class GetCurrentState(chatId: ChatId) extends BotDsl[Option[BotStateId]]

  case class SaveState(chatId: ChatId, botStateId: BotStateId) extends BotDsl[Unit]

  case class Reply(botToken: BotToken, chatId: ChatId, message: Message.Payload) extends BotDsl[Unit]

  case class RaiseError(botError: BotError) extends BotDsl[Unit]

  def loadScenario(botToken: BotToken): BotScript[Option[GraphBotScenario]] =
    liftF(LoadScenario(botToken))

  def getCurrentState(chatId: ChatId): BotScript[Option[BotStateId]] =
    liftF(GetCurrentState(chatId))

  def saveState(chatId: ChatId, botStateId: BotStateId): BotScript[Unit] =
    liftF(SaveState(chatId, botStateId))

  def reply(botToken: BotToken, chatId: ChatId, message: Message.Payload): BotScript[Unit] =
    liftF(Reply(botToken, chatId, message))

  def raiseError(botError: BotError): BotScript[Unit] =
    liftF(RaiseError(botError))
}

