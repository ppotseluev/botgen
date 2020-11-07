package botgen

import com.softwaremill.tagging._

package object model {
  type ChatId = String @@ Tags.ChatId
  type BotStateId = String @@ Tags.BotStateId
  type BotCommand = String @@ Tags.BotCommand
  type BotKey = String @@ Tags.BotKey
  type BotToken = String @@ Tags.BotToken
}
