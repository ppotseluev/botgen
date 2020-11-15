package botgen.bot

import botgen.model.{BotToken, Message}

case class BotInput(botToken: BotToken,
                    message: Message)