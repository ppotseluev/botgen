package botgen.model

case class Message(payload: Message.Payload)

object Message {
  case class Payload(text: String, images: Seq[Image])
}