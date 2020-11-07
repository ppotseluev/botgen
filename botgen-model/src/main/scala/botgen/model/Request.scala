package botgen.model

sealed trait Request

object Request {

  case class ProcessMessage(botToken: BotToken,
                            message: Message) extends Request {
    def botKey: BotKey = botToken.taggedWith[Tags.BotKey] //TODO hash botToken
  }

}
