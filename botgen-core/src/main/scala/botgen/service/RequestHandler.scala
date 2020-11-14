package botgen.service

import botgen.model.Request

trait RequestHandler[F[_]] {
  def handle[T](request: Request[T]): F[T]
}
