package botgen

import botgen.bot.scenario.GraphBotScenario
import botgen.model.{BotInfo, BotKey, ChatId}

package object dao {
  type BotScenarioDao[F[_]] = KeyValueDao[F, BotKey, GraphBotScenario]
  type BotStateDao[F[_]] = KeyValueDao[F, ChatId, BotInfo]
}
