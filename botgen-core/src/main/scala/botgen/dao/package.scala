package botgen

import botgen.model.{BotDefinition, BotInfo, BotKey, ChatId}

package object dao {
  type BotDefinitionDao[F[_]] = KeyValueDao[F, BotKey, BotDefinition]
  type BotStateDao[F[_]] = KeyValueDao[F, (ChatId, BotKey), BotInfo]
}
