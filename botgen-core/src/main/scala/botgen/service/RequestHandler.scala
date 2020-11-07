package botgen.service

import botgen.model.Request

trait RequestHandler[F[_]] {
  def handle(request: Request): F[Unit]
}
