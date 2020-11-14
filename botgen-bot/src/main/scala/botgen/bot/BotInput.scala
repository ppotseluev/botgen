package botgen.bot

import botgen.model.{BotKey, BotToken, Message, Tags}

case class BotInput(botToken: BotToken,
                    message: Message) {

  //  FIXME remove ?
  def botKey: BotKey = botToken.taggedWith[Tags.BotKey] //TODO hash botToken
}
