package botgen.serialization

import botgen.bot.scenario.{ExpectedInputPredicate, GraphBotScenario}
import botgen.bot.scenario.GraphBotScenario.{BotGraph, EdgeLabel, Node}
import botgen.model.BotStateId
import botgen.serialization.GraphBotScenarioView.Edge
import scalax.collection.Graph
import scalax.collection.edge.LDiEdge
import GraphBotScenario.EdgeImplicits._

case class GraphBotScenarioView(startFrom: BotStateId,
                                nodes: Seq[Node],
                                edges: Seq[Edge]) {
  private lazy val nodesMap: Map[BotStateId, Node] =
    nodes.map(node => node.id -> node).toMap

  def asModel: GraphBotScenario = {
    val graph: BotGraph = Graph.from(nodes, edges.zipWithIndex.map((prepare _).tupled))
    new GraphBotScenario(graph, startFrom)
  }

  private def prepare(edge: Edge, order: Int): LDiEdge[Node] = {
    val label = EdgeLabel(order, edge.expectedInputPredicate)
    LDiEdge(nodesMap(edge.from), nodesMap(edge.to))(label)
  }
}

object GraphBotScenarioView {

  case class Edge(from: BotStateId,
                  to: BotStateId,
                  expectedInputPredicate: ExpectedInputPredicate)

  def fromModel(model: GraphBotScenario): GraphBotScenarioView = {
    GraphBotScenarioView(
      startFrom = model.startFrom,
      nodes = model.graph.nodes.map(_.value).toSeq,
      edges = model.graph.edges
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
