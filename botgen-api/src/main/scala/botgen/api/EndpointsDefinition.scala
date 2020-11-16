package botgen.api

import botgen.api.TapirSchemas._
import botgen.api.model.telegram.Update
import botgen.model.{BotDefinition, BotToken}
import botgen.serialization.JsonCodecInstances._
import sttp.model.StatusCode
import sttp.tapir._
import sttp.tapir.json.circe._

object EndpointsDefinition {

  private val baseEndpoint = {
    import io.circe.generic.auto._
    endpoint.errorOut(
      oneOf[ApiError](
        statusMapping(StatusCode.NotFound, jsonBody[ApiError.NotFound].description("not found")),
        statusMapping(StatusCode.InternalServerError, jsonBody[ApiError.InternalServerError].description("internal server error")),
        statusMapping(StatusCode.BadRequest, stringBody.description("bad request"))
      )
    )
  }

  private val botEndpoint = baseEndpoint.in("bots" / path[BotToken]("token"))

  val postTelegramUpdate: Endpoint[(Update, BotToken), ApiError, Unit, Nothing] =
    baseEndpoint.in("telegram")
      .post
      .in(jsonBody[Update])
      .in(query[BotToken]("token"))

  val upsertBot: Endpoint[(BotToken, BotDefinition), ApiError, Unit, Nothing] =
    botEndpoint
      .put
      .in(jsonBody[BotDefinition].example(Examples.botDefinition))

  val getBot: Endpoint[BotToken, ApiError, BotDefinition, Nothing] =
    botEndpoint
      .get
      .out(jsonBody[BotDefinition])
}
