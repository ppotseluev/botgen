package botgen

import botgen.bot.BotDsl.BotScript

package object bot {
  type BotLogic = BotInput => BotScript[Unit]
}
