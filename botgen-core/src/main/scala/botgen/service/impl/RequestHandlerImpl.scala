package botgen.service.impl

import botgen.bot.BotDsl
import botgen.bot.BotDsl.BotScript
import botgen.model.Request
import botgen.service.RequestHandler
import cats.{Monad, ~>}

class RequestHandlerImpl[F[_] : Monad](botLogic: Request.ProcessMessage => BotScript[Unit],
                                       botCompiler: BotDsl ~> F)
  extends RequestHandler[F] {

  override def handle(request: Request): F[Unit] = request match {
    case processMessageRequest: Request.ProcessMessage =>
      botLogic(processMessageRequest).foldMap(botCompiler)
  }
}
