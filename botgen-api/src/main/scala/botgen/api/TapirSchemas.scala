package botgen.api

import java.net.URLDecoder
import java.nio.charset.StandardCharsets

import botgen.bot.scenario.GraphBotScenario
import botgen.model.{BotStateId, BotToken, Tags}
import botgen.serialization.GraphBotScenarioView
import sttp.tapir.{Codec, Schema}
import sttp.tapir.Codec.stringCodec
import sttp.tapir.CodecFormat.TextPlain
import com.softwaremill.tagging._
import sttp.tapir.SchemaType.SString

object TapirSchemas {
  implicit val botTokenSchema: Schema[BotToken] = Schema(SString)
  implicit val botStateId: Schema[BotStateId] = Schema(SString)

  implicit val graphBotScenarioSchema: Schema[GraphBotScenario] =
    implicitly[Schema[GraphBotScenarioView]]
      .modify(_.asModel)(identity)
      .asRequired

  implicit val botToken: Codec[String, BotToken, TextPlain] =
    stringCodec(URLDecoder.decode(_, StandardCharsets.UTF_8.name).taggedWith[Tags.BotToken])
}
