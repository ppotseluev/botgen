package botgen.bot.scenario

import botgen.bot.BotState
import botgen.model.{BotStateId, Message}

trait Scenario {
  def startFrom: BotStateId

  def transit(stateId: BotStateId, command: Message.Payload): Option[BotState]

  def get(stateId: BotStateId): Option[BotState]
}
