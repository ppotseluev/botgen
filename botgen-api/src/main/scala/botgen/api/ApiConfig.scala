package botgen.api

import botgen.server.config.{HttpClientConfig, MySqlConfig}

case class ApiConfig(mySql: MySqlConfig,
                     telegramClient: HttpClientConfig,
                     sslConfig: SslConfig,
                     webhookUrl: String,
                     apiPort: Int,
                     hashSalt: String)
