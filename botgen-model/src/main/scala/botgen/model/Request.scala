package botgen.model

sealed trait Request

object Request {
  case class ProcessMessage(botKey: BotKey,
                            message: Message) extends Request
}
