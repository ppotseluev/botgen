package botgen.api

import botgen.bot.Action
import botgen.bot.scenario.ExpectedInputPredicate
import botgen.bot.scenario.GraphBotScenario.Node
import botgen.model.{BotDefinition, Tags}
import botgen.serialization.GraphBotScenarioView
import botgen.serialization.GraphBotScenarioView.Edge
import com.softwaremill.tagging._

object Examples {

  private val start = "start".taggedWith[Tags.BotStateId]
  private val about = "about".taggedWith[Tags.BotStateId]
  private val name = "name".taggedWith[Tags.BotStateId]

  private val botGraph = new GraphBotScenarioView(
    startFrom = start,
    states = Seq(
      Node(
        start,
        Action.Reply("Hi! I'm a bot created with botgen service")
      ),
      Node(
        about,
        Action.Reply("See info about project https://github.com/ppotseluev/botgen")
      ),
      Node(
        name,
        Action.Reply("My name is botgen_example_bot")
      )
    ),
    transitions = Seq(
      Edge(
        from = start,
        to = about,
        expectedInputPredicate = ExpectedInputPredicate.TextIsEqualTo("Info about botgen project")
      ),
      Edge(
        from = start,
        to = name,
        expectedInputPredicate = ExpectedInputPredicate.TextIsEqualTo("What's your name?")
      ),
      Edge(
        from = name,
        to = start,
        expectedInputPredicate = ExpectedInputPredicate.TextIsEqualTo("back")
      ),
      Edge(
        from = about,
        to = start,
        expectedInputPredicate = ExpectedInputPredicate.TextIsEqualTo("back")
      )
    ),
    globalCommands = Map(
      "/start".taggedWith[Tags.BotCommand] -> Action.GoTo(start),
      "/help".taggedWith[Tags.BotCommand] -> Action.Reply("Here should be some help message")
    )
  ).asModel

  val botDefinition: BotDefinition = BotDefinition(botGraph)
}
