package botgen.model

sealed trait Request[T]

object Request {

  case class ProcessMessage(botToken: BotToken,
                            message: Message) extends Request[Unit] {
    def botKey: BotKey = botToken.taggedWith[Tags.BotKey] //TODO hash botToken
  }

  case class UpsertBot(botToken: BotToken,
                       botDefinition: BotDefinition) extends Request[Unit]

  case class GetBot(botKey: BotKey) extends Request[Option[BotDefinition]]
}
