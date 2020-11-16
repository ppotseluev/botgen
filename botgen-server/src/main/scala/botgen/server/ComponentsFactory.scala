package botgen.server

import botgen.bot.Bot.FallbackPolicy.Ignore
import botgen.bot.{Bot, BotLogic}
import botgen.client.TelegramClient
import botgen.client.impl.HttpTelegramClient
import botgen.compiler.{BotCompiler, BotCompilerImpl}
import botgen.dao.impl.MySqlKeyValueDao
import botgen.dao.{BotDefinitionDao, BotStateDao, Schema}
import botgen.model._
import botgen.serialization.JsonCodecInstances._
import botgen.serialization.StringCodecInstances._
import botgen.server.config.{HttpClientConfig, MySqlConfig}
import botgen.service.impl.{RequestHandlerImpl, TelegramChatService}
import botgen.service.{ChatService, RequestHandler}
import cats.effect.{Async, Concurrent, ContextShift, Resource}
import cats.{ApplicativeError, Monad}
import com.github.t3hnar.bcrypt._
import com.softwaremill.tagging._
import doobie.util.transactor.Transactor
import sttp.client.SttpBackend
import sttp.client.asynchttpclient.WebSocketHandler
import sttp.client.asynchttpclient.cats.AsyncHttpClientCatsBackend


object ComponentsFactory { //TODO add logging for some components

  def keyCalculator(salt: String)(token: BotToken): BotKey =
    token.bcryptBounded(salt).taggedWith[Tags.BotKey]

  def requestHandler[F[_] : Monad](botLogic: BotLogic,
                                   botCompiler: BotCompiler[F],
                                   botDefinitionDao: BotDefinitionDao[F],
                                   chatService: ChatService[F],
                                   webhookUrl: BotToken => String,
                                   toKey: BotToken => BotKey): RequestHandler[F] =
    new RequestHandlerImpl[F](botLogic, botCompiler, botDefinitionDao, chatService, webhookUrl, toKey)

  def telegramClient[F[_] : Concurrent](config: HttpClientConfig)
                                       (implicit cs: ContextShift[F]): Resource[F, TelegramClient[F]] =
    createHttpClient[F, TelegramClient[F]](implicit sttp => new HttpTelegramClient(config.url))

  def chatService[F[_]](telegramClient: TelegramClient[F]): ChatService[F] =
    new TelegramChatService[F](telegramClient)

  def botCompiler[F[_]](botDefinitionDao: BotDefinitionDao[F],
                        botStateDao: BotStateDao[F],
                        chatService: ChatService[F],
                        toKey: BotToken => BotKey)
                       (implicit F: ApplicativeError[F, Throwable]): BotCompiler[F] =
    new BotCompilerImpl[F](botDefinitionDao, botStateDao, chatService, toKey)

  lazy val botLogic: BotLogic = new Bot(fallbackPolicy = Ignore)

  def mysqlBotDefinitionDao[F[_] : Async](mySqlConfig: MySqlConfig,
                                          tableName: String = "bots")
                                         (implicit cs: ContextShift[F]): BotDefinitionDao[F] = {
    implicit val keySchema: Schema.String[BotKey] = Schema.String(implicitly)
    implicit val scenarioSchema: Schema[BotDefinition] = Schema.Json(implicitly)
    new MySqlKeyValueDao(tableName, transactor(mySqlConfig))
  }

  def mysqlBotStateDao[F[_] : Async](mySqlConfig: MySqlConfig,
                                     tableName: String = "states")
                                    (implicit cs: ContextShift[F]): BotStateDao[F] = {
    implicit val keySchema: Schema.String[(ChatId, BotKey)] = Schema.String(implicitly)
    implicit val scenarioSchema: Schema[BotInfo] = Schema.Json(implicitly)
    new MySqlKeyValueDao(tableName, transactor(mySqlConfig))
  }

  private def transactor[F[_] : Async](mySqlConfig: MySqlConfig)
                                      (implicit cs: ContextShift[F]) =
    Transactor.fromDriverManager[F](
      "com.mysql.cj.jdbc.Driver",
      mySqlConfig.url,
      mySqlConfig.user,
      mySqlConfig.password
    )

  private def createHttpClient[F[_] : Concurrent, C](create: SttpBackend[F, Nothing, WebSocketHandler] => C)
                                                    (implicit cs: ContextShift[F]): Resource[F, C] =
    AsyncHttpClientCatsBackend
      .resource[F]()
      .map(create)
}
