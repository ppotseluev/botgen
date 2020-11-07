package botgen.service.impl

import botgen.client.TelegramClient
import botgen.model.{BotToken, ChatId, Message}
import botgen.service.ChatService

class TelegramChatService[F[_]](telegramClient: TelegramClient[F])
  extends ChatService[F] {

  override def send(botToken: BotToken)
                   (chatId: ChatId)
                   (payload: Message.Payload): F[Unit] = {
    val message = TelegramClient.MessageSource(chatId, payload.text)
    telegramClient.send(botToken)(message)
  }
}
