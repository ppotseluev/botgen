package botgen.bot

sealed trait Action

object Action {

  case class Reply(text: String) extends Action

}
