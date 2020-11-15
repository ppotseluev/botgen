package botgen.service

import botgen.model.{BotToken, ChatId, Message}

trait ChatService[F[_]] {
  def send(botToken: BotToken)
          (chatId: ChatId)
          (payload: Message.Payload): F[Unit]

  def setWebhook(botToken: BotToken, url: String): F[Unit]
}
