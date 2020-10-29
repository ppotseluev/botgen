package botgen

import com.softwaremill.tagging._

package object model {
  type Url = String @@ Tags.Url
  type ChatId = String @@ Tags.ChatId
  type BotStateId = String @@ Tags.BotStateId
  type BotCommand = String @@ Tags.BotCommand
}
