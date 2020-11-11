package botgen.service.impl

import botgen.bot.BotLogic
import botgen.compiler.BotCompiler
import botgen.model.Request
import botgen.service.RequestHandler
import cats.Monad

class RequestHandlerImpl[F[_] : Monad](botLogic: BotLogic,
                                       botCompiler: BotCompiler[F])
  extends RequestHandler[F] {

  override def handle(request: Request): F[Unit] = request match {
    case processMessageRequest: Request.ProcessMessage =>
      botLogic(processMessageRequest).foldMap(botCompiler)
  }
}
