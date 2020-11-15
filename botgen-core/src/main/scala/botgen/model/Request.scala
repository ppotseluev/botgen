package botgen.model

sealed trait Request[T]

object Request {

  case class ProcessMessage(botToken: BotToken,
                            message: Message) extends Request[Unit]

  case class UpsertBot(botToken: BotToken,
                       botDefinition: BotDefinition) extends Request[Unit]

  case class GetBot(botToken: BotToken) extends Request[Option[BotDefinition]]
}
