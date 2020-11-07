package botgen.model

case class Message(chatId: ChatId,
                   payload: Message.Payload)

object Message {

  case class Payload(text: String, availableCommands: Seq[BotCommand])

}