package botgen.bot

import botgen.model.Message

sealed trait Action

object Action {

  case class Reply(payload: Message.Payload) extends Action

}
