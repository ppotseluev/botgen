package botgen.serialization

import botgen.bot.Action
import botgen.bot.scenario.GraphBotScenario.Node
import botgen.bot.scenario.{ExpectedInputPredicate, GraphBotScenario}
import botgen.model.{BotCommand, BotStateId, Message, Tags}
import botgen.serialization.GraphBotScenarioView.Edge
import botgen.utils.CirceUtils._
import cats.syntax.invariant._
import com.softwaremill.tagging._
import io.circe.Encoder._
import io.circe.Decoder._
import io.circe.Codec
import io.circe.generic.semiauto._

object JsonCodecInstances {
  val stringCodec: Codec[String] = Codec.from(implicitly, implicitly)

  implicit lazy val botStateIdCodec: Codec[BotStateId] =
    stringCodec.imap(_.taggedWith[Tags.BotStateId])(identity)

  implicit lazy val botCommandCodec: Codec[BotCommand] =
    stringCodec.imap(_.taggedWith[Tags.BotCommand])(identity)

  implicit lazy val messagePayloadCodec: Codec[Message.Payload] =
    deriveCodec

  implicit lazy val expectedInputPredicateCodec: Codec[ExpectedInputPredicate] =
    deriveCodec

  implicit lazy val actionCodec: Codec[Action] =
    deriveCodec

  implicit lazy val nodeCodec: Codec[Node] =
    deriveCodec

  implicit lazy val edgeCodec: Codec[Edge] = deriveCodec

  implicit lazy val botGraphScenarioCodec: Codec[GraphBotScenario] = {
    val codec: Codec[GraphBotScenarioView] = deriveCodec[GraphBotScenarioView]
    codec.imap(_.asModel)(GraphBotScenarioView.fromModel)
  }
}
