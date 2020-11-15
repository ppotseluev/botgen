package botgen.bot.scenario

import botgen.bot.Action.GoTo
import botgen.bot.scenario.GraphBotScenario.BotGraph
import botgen.bot.{Action, BasicAction, BotState}
import botgen.model.{BotCommand, BotStateId, Message, Tags}
import com.softwaremill.tagging._
import scalax.collection.Graph
import scalax.collection.edge.LBase.LEdgeImplicits
import scalax.collection.edge.LDiEdge
import cats.syntax.option._


class GraphBotScenario(val graph: BotGraph,
                       val startFrom: BotStateId,
                       val globalCommands: Map[BotCommand, Action]) {

  import GraphBotScenario.EdgeImplicits._

  private val states: Map[BotStateId, graph.NodeT] =
    graph.nodes
      .map(node => node.id -> node)
      .toMap

  private def extractAvailableCommands(node: graph.NodeT): Seq[BotCommand] =
    node.outgoing.toSeq.sortBy(_.order).flatMap(asCommand)

  private def toBotState(node: graph.NodeT): Option[BotState] =
    node.action match {
      case action: BasicAction =>
        BotState(
          id = node.id,
          action = action,
          availableCommands = extractAvailableCommands(node)
        ).some
      case Action.GoTo(state) =>
        get(state)
    }

  private def asCommand(edge: graph.EdgeT): Option[BotCommand] =
    edge.expectedInputPredicate match {
      case ExpectedInputPredicate.TextIsEqualTo(expectedText) =>
        Some(expectedText.taggedWith[Tags.BotCommand])
    }

  private def isMatched(command: Message.Payload)
                       (edge: graph.EdgeT): Boolean =
    Matcher.isMatched(command)(edge.expectedInputPredicate)

  def transit(stateId: BotStateId,
              command: Message.Payload): Option[BotState] =
    states
      .get(stateId)
      .flatMap(_.outgoing.find(isMatched(command)))
      .map(_.to)
      .flatMap(toBotState)
      .orElse(globalState(stateId, command))

  private def get(stateId: BotStateId): Option[BotState] =
    states.get(stateId).flatMap(toBotState)

  private def globalState(currentStateId: BotStateId,
                          command: Message.Payload): Option[BotState] =
    globalCommands.get(command.text.taggedWith[Tags.BotCommand]) match {
      case Some(action: BasicAction) => get(currentStateId).map(_.copy(action = action))
      case Some(GoTo(anotherState)) => get(anotherState)
      case None => None
    }
}

object GraphBotScenario {

  case class EdgeLabel(order: Int, expectedInputPredicate: ExpectedInputPredicate)

  case class Node(id: BotStateId, action: Action)

  type BotGraph = Graph[Node, LDiEdge]

  object EdgeImplicits extends LEdgeImplicits[EdgeLabel]

}
