package botgen.bot.scenario

import botgen.model.Message

object Matcher {
  def isMatched(input: Message.Payload)
               (predicate: ExpectedInputPredicate): Boolean =
    predicate match {
      case ExpectedInputPredicate.TextIsEqualTo(expectedText) =>
        input.text == expectedText
    }
}
