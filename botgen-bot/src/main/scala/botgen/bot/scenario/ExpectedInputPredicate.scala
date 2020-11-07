package botgen.bot.scenario

sealed trait ExpectedInputPredicate

object ExpectedInputPredicate {
  case class TextIsEqualTo(expectedText: String) extends ExpectedInputPredicate
}