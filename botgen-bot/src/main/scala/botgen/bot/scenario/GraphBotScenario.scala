package botgen.bot.scenario

import botgen.bot.scenario.GraphBotScenario.{BotGraph, Node}
import botgen.bot.{Action, BotState}
import botgen.model.{BotCommand, BotStateId, Message, Tags}
import com.softwaremill.tagging._
import scalax.collection.Graph
import scalax.collection.edge.LBase.LEdgeImplicits
import scalax.collection.edge.LDiEdge


class GraphBotScenario(val graph: BotGraph,
                       val startFrom: BotStateId) {

  import GraphBotScenario.EdgeImplicits._

  private val states: Map[BotStateId, graph.NodeT] =
    graph.nodes
      .map(node => node.id -> node)
      .toMap

  private def toBotState(node: graph.NodeT): BotState =
    BotState(
      id = node.id,
      action = node.action,
      availableCommands = node.outgoing.toSeq.sortBy(_.order).flatMap(asCommand)
    )

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
      .map(toBotState)

  def get(stateId: BotStateId): Option[BotState] =
    states
      .get(stateId)
      .map(toBotState)
}

object GraphBotScenario {

  case class EdgeLabel(order: Int, expectedInputPredicate: ExpectedInputPredicate)

  case class Node(id: BotStateId, action: Action)

  type BotGraph = Graph[Node, LDiEdge]

  object EdgeImplicits extends LEdgeImplicits[EdgeLabel]

}
