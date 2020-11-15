package botgen.bot

import botgen.bot.Bot.FallbackPolicy
import botgen.bot.BotDsl._
import botgen.bot.scenario.GraphBotScenario
import botgen.model.Message
import cats.syntax.applicative._

/**
 * Describes bot logic using [[BotDsl]]
 */
class Bot(fallbackPolicy: FallbackPolicy)
  extends BotLogic {

  override def apply(botInput: BotInput): BotScript[Unit] =
    loadScenario(botInput.botToken).flatMap {
      case Some(scenario) => process(botInput, scenario)
      case None => raiseError(BotError.ScenarioNotFound)
    }

  private def process(botInput: BotInput,
                      scenario: GraphBotScenario): BotScript[Unit] = {
    val BotInput(_, Message(chatId, payload)) = botInput
    for {
      currentStateId <- getCurrentState(chatId).map(_.getOrElse(scenario.startFrom))
      _ <- scenario.transit(currentStateId, payload) match {
        case Some(newState) => process(botInput, newState)
        case None => fallbackPolicy match {
          case FallbackPolicy.Ignore => ().pure[BotScript]
        }
      }
    } yield ()
  }

  private def process(botInput: BotInput,
                      newState: BotState): BotScript[Unit] = for {
    _ <- saveState(botInput.message.chatId, newState.id)
    _ <- newState.action match {
      case Action.Reply(text) =>
        val payload = Message.Payload(text, newState.availableCommands)
        reply(botInput.botToken, botInput.message.chatId, payload)
    }
  } yield ()
}

object Bot {

  /**
   * Defines wow to handle unexpected commands
   */
  sealed trait FallbackPolicy

  object FallbackPolicy {

    case object Ignore extends FallbackPolicy

  }

}