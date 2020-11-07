package botgen.bot

import botgen.bot.scenario.Scenario
import botgen.model._
import cats.free.Free
import cats.free.Free.liftF

sealed trait BotDsl[T]

object BotDsl {
  /**
   * Free monad based bot's EDSL
   */
  type BotScript[T] = Free[BotDsl, T]

  case class LoadScenario(botKey: BotKey) extends BotDsl[Option[Scenario]]

  case class GetCurrentState(chatId: ChatId) extends BotDsl[Option[BotStateId]]

  case class SaveState(chatId: ChatId, botStateId: BotStateId) extends BotDsl[Unit]

  case class Reply(chatId: ChatId, message: Message.Payload) extends BotDsl[Unit]

  case class RaiseError(botError: BotError) extends BotDsl[Unit]

  def loadScenario(botKey: BotKey): BotScript[Option[Scenario]] =
    liftF(LoadScenario(botKey))

  def getCurrentState(chatId: ChatId): BotScript[Option[BotStateId]] =
    liftF(GetCurrentState(chatId))

  def saveState(chatId: ChatId, botStateId: BotStateId): BotScript[Unit] =
    liftF(SaveState(chatId, botStateId))

  def reply(chatId: ChatId, message: Message.Payload): BotScript[Unit] =
    liftF(Reply(chatId, message))

  def raiseError(botError: BotError): BotScript[Unit] =
    liftF(RaiseError(botError))
}

