package botgen.bot

sealed trait BotError

object BotError {
  case object ScenarioNotFound extends BotError
}