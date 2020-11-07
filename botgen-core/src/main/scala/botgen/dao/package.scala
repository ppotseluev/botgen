package botgen

import botgen.bot.scenario.Scenario
import botgen.model.{BotInfo, BotKey, ChatId}

package object dao {
  type BotScenarioDao[F[_]] = KeyValueDao[F, BotKey, Scenario]
  type BotStateDao[F[_]] = KeyValueDao[F, ChatId, BotInfo]
}
