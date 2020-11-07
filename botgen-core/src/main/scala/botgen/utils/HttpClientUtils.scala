package botgen.utils

import cats.MonadError
import cats.syntax.applicative._
import cats.syntax.applicativeError._
import cats.syntax.flatMap._
import sttp.client.Response
import sttp.model.StatusCode

object HttpClientUtils {

  implicit class RichResponse[F[_], T](val responseF: F[Response[T]]) extends AnyVal {

    def checkStatusCode(isSuccess: StatusCode => Boolean = _.isSuccess)
                       (implicit F: MonadError[F, Throwable]): F[Response[T]] =
      responseF.flatMap { response =>
        if (isSuccess(response.code))
          response.pure
        else
          HttpCodeException(response.code.code, response.statusText).raiseError
      }
  }

}
