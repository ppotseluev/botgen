package botgen.bot

import botgen.model.BotStateId

sealed trait Action

sealed trait BasicAction extends Action

object Action {

  case class Reply(text: String) extends BasicAction

  case class GoTo(state: BotStateId) extends Action

}
