package botgen.bot

import botgen.bot.Bot.FallbackPolicy
import botgen.bot.BotDsl.{BotScript, getCurrentState, loadScenario, raiseError, reply, saveState}
import botgen.bot.scenario.Scenario
import botgen.model.{Message, Request}
import cats.syntax.applicative._

/**
 * Describes bot logic using [[BotDsl]]
 */
class Bot(fallbackPolicy: FallbackPolicy) {
  def process(request: Request.ProcessMessage): BotScript[Unit] =
    loadScenario(request.botKey).flatMap {
      case Some(scenario) => process(request, scenario)
      case None => raiseError(BotError.ScenarioNotFound)
    }

  private def process(request: Request.ProcessMessage,
                      scenario: Scenario): BotScript[Unit] = {
    val Request.ProcessMessage(_, Message(chatId, payload)) = request
    for {
      currentStateId <- getCurrentState(chatId).map(_.getOrElse(scenario.startFrom))
      _ <- scenario.transit(currentStateId, payload) match {
        case Some(newState) => process(request, newState)
        case None => fallbackPolicy match {
          case FallbackPolicy.Ignore => ().pure[BotScript]
        }
      }
    } yield ()
  }

  private def process(request: Request.ProcessMessage,
                      newState: BotState): BotScript[Unit] = for {
    _ <- saveState(request.message.chatId, newState.id)
    _ <- newState.action match {
      case Action.Reply(payload) => reply(request.message.chatId, payload)
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