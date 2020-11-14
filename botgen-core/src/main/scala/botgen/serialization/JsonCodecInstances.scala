package botgen.serialization

import botgen.bot.Action
import botgen.bot.scenario.GraphBotScenario.Node
import botgen.bot.scenario.{ExpectedInputPredicate, GraphBotScenario}
import botgen.model._
import botgen.serialization.GraphBotScenarioView.Edge
import botgen.utils.CirceUtils._
import cats.syntax.invariant._
import com.softwaremill.tagging._
import io.circe.Codec
import io.circe.Decoder._
import io.circe.Encoder._
import io.circe.generic.semiauto._

object JsonCodecInstances {
  val stringCodec: Codec[String] = Codec.from(implicitly, implicitly)

  implicit val botStateIdCodec: Codec[BotStateId] =
    stringCodec.imap(_.taggedWith[Tags.BotStateId])(identity)

  implicit val botCommandCodec: Codec[BotCommand] =
    stringCodec.imap(_.taggedWith[Tags.BotCommand])(identity)

  implicit val messagePayloadCodec: Codec[Message.Payload] =
    deriveCodec

  implicit val expectedInputPredicateCodec: Codec[ExpectedInputPredicate] =
    deriveCodec

  implicit val actionCodec: Codec[Action] =
    deriveCodec

  implicit val nodeCodec: Codec[Node] =
    deriveCodec

  implicit val edgeCodec: Codec[Edge] = deriveCodec

  implicit val botGraphScenarioCodec: Codec[GraphBotScenario] = {
    val codec: Codec[GraphBotScenarioView] = deriveCodec[GraphBotScenarioView]
    codec.imap(_.asModel)(GraphBotScenarioView.fromModel)
  }

  implicit val botInfoCodec: Codec[BotInfo] = deriveCodec

  implicit val botDefinitionCodec: Codec[BotDefinition] = deriveCodec
}
