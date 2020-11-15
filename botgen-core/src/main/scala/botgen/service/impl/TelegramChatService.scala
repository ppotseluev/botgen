package botgen.service.impl

import botgen.client.TelegramClient
import botgen.client.TelegramClient.{ReplyMarkup, KeyboardButton}
import botgen.model.{BotCommand, BotToken, ChatId, Message}
import botgen.service.ChatService

class TelegramChatService[F[_]](telegramClient: TelegramClient[F])
  extends ChatService[F] {

  override def send(botToken: BotToken)
                   (chatId: ChatId)
                   (payload: Message.Payload): F[Unit] = {
    val keyboard = buildKeyboard(payload.availableCommands)
    val message = TelegramClient.MessageSource(chatId, payload.text, Some(keyboard))
    telegramClient.send(botToken)(message)
  }

  private def buildKeyboard(availableCommands: Seq[BotCommand]): ReplyMarkup =
    if (availableCommands.isEmpty)
      ReplyMarkup(removeKeyboard = Some(true))
    else {
      val buttons = availableCommands
        .map(KeyboardButton.apply)
        .map(Seq(_))
      ReplyMarkup(keyboard = Some(buttons))
    }

  override def setWebhook(botToken: BotToken, url: String): F[Unit] =
    telegramClient.setWebhook(botToken, url)
}
