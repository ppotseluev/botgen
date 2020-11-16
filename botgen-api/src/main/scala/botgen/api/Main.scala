package botgen.api

import cats.effect.{IO, _}
import com.typesafe.config.{Config, ConfigFactory}
import com.typesafe.scalalogging.StrictLogging
import net.ceedubs.ficus.Ficus._
import net.ceedubs.ficus.readers.ArbitraryTypeReader._
import scala.concurrent.ExecutionContext.Implicits.global

object Main extends IOApp with StrictLogging {
  private val config: Config = ConfigFactory.load()
  private val apiConfig: ApiConfig = config.as[ApiConfig]

  private val apiApp = new ApiApp[IO](apiConfig)

  override def run(args: List[String]): IO[ExitCode] =
    apiApp.runServer
}
