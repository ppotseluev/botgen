package botgen.model

sealed trait Request

object Request {
  case class ProcessMessage(botToken: BotToken,
                            message: Message) extends Request {
    def botKey: BotKey = ??? //TODO hash botToken
  }
}
