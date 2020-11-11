package botgen

import botgen.bot.BotDsl.BotScript
import botgen.model.Request

package object bot {
  type BotLogic = Request.ProcessMessage => BotScript[Unit]
}
