package botgen.bot

import botgen.model.{BotCommand, BotStateId}

case class BotState(id: BotStateId,
                    action: Action,
                    availableCommands: Seq[BotCommand])
