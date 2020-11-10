package botgen.serialization

import botgen.bot.scenario.{ExpectedInputPredicate, GraphBotScenario}
import botgen.bot.scenario.GraphBotScenario.{BotGraph, EdgeLabel, Node}
import botgen.model.BotStateId
import botgen.serialization.GraphBotScenarioView.Edge
import scalax.collection.Graph
import scalax.collection.edge.LDiEdge
import GraphBotScenario.EdgeImplicits._

case class GraphBotScenarioView(startFrom: BotStateId,
                                states: Seq[Node],
                                transitions: Seq[Edge]) {
  private lazy val statesMap: Map[BotStateId, Node] =
    states.map(node => node.id -> node).toMap

  def asModel: GraphBotScenario = {
    val graph: BotGraph = Graph.from(states, transitions.zipWithIndex.map((prepare _).tupled))
    new GraphBotScenario(graph, startFrom)
  }

  private def prepare(edge: Edge, order: Int): LDiEdge[Node] = {
    val label = EdgeLabel(order, edge.expectedInputPredicate)
    LDiEdge(statesMap(edge.from), statesMap(edge.to))(label)
  }
}

object GraphBotScenarioView {

  case class Edge(from: BotStateId,
                  to: BotStateId,
                  expectedInputPredicate: ExpectedInputPredicate)

  def fromModel(model: GraphBotScenario): GraphBotScenarioView = {
    GraphBotScenarioView(
      startFrom = model.startFrom,
      states = model.graph.nodes.map(_.value).toSeq,
      transitions = model.graph.edges
        .toSeq
        .sortBy(_.order)
        .map { edge =>
          Edge(
            from = edge.from.id,
            to = edge.to.id,
            expectedInputPredicate = edge.expectedInputPredicate
          )
        }
    )
  }
}
