package botgen.api

import cats.effect.{ContextShift, Sync}
import cats.implicits.toSemigroupKOps
import org.http4s.implicits.http4sKleisliResponseSyntaxOptionT
import org.http4s.{HttpApp, HttpRoutes}
import sttp.tapir.docs.openapi._
import sttp.tapir.openapi.circe.yaml._
import sttp.tapir.server.http4s._
import sttp.tapir.swagger.http4s.SwaggerHttp4s

class ApiRoutes[F[_] : Sync](endpointsImplementation: EndpointsImplementation[F])
                            (implicit cs: ContextShift[F]) {

  import EndpointsDefinition._
  import endpointsImplementation._

  private val postTelegramRoutes: HttpRoutes[F] =
    postTelegramUpdate.toRoutes((handleTelegramEvent _).tupled)

  private val upsertBotRoutes: HttpRoutes[F] =
    upsertBot.toRoutes((handleUpsertBot _).tupled)

  private val getBotRoutes: HttpRoutes[F] =
    getBot.toRoutes(handleGetBot)

  private val openapiYaml: String =
    List(upsertBot, getBot)
      .toOpenAPI("Botgen", "1.0")
      .toYaml

  val buildHttpApp: HttpApp[F] =
    Seq(
      new SwaggerHttp4s(openapiYaml).routes,
      postTelegramRoutes,
      upsertBotRoutes,
      getBotRoutes
    ).reduce(_ <+> _).orNotFound
}
