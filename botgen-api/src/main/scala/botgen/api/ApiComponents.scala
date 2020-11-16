package botgen.api

import botgen.client.TelegramClient
import botgen.dao.{BotDefinitionDao, BotStateDao}
import botgen.model.{BotKey, BotToken}
import botgen.server.ComponentsFactory
import cats.effect.{Concurrent, ContextShift, Resource}

class ApiComponents[F[_] : Concurrent](config: ApiConfig)
                                      (implicit cs: ContextShift[F]) {

  private val componentsFactory = ComponentsFactory

  private val botKeyCalculator: BotToken => BotKey =
    componentsFactory.keyCalculator(config.hashSalt) //TODO generate random for every hash

  private val botStateDao: BotStateDao[F] =
    componentsFactory.mysqlBotStateDao(config.mySql)

  private val botDefinitionDao: BotDefinitionDao[F] =
    componentsFactory.mysqlBotDefinitionDao(config.mySql)

  private val tgClientResource: Resource[F, TelegramClient[F]] =
    componentsFactory.telegramClient(config.telegramClient)

  val endpointsImplementation: Resource[F, EndpointsImplementation[F]] = tgClientResource.map { tgClient =>
    val chatService = componentsFactory.chatService(tgClient)
    val botCompiler = componentsFactory.botCompiler(botDefinitionDao, botStateDao, chatService, botKeyCalculator)
    val botLogic = componentsFactory.botLogic
    val requestHandler = componentsFactory.requestHandler(
      botLogic = botLogic,
      botCompiler = botCompiler,
      botDefinitionDao = botDefinitionDao,
      chatService = chatService,
      webhookUrl = token => config.webhookUrl.replace("{token}", token),
      toKey = botKeyCalculator
    )
    new EndpointsImplementation(requestHandler)
  }
}