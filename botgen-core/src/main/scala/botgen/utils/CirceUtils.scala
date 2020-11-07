package botgen.utils

import cats.Invariant
import io.circe.Codec

object CirceUtils {
  implicit val CodecInvariant: Invariant[Codec] = new Invariant[Codec] {
    override def imap[A, B](fa: Codec[A])
                           (f: A => B)
                           (g: B => A): Codec[B] =
      Codec.from(
        decodeA = fa.map(f),
        encodeA = fa.contramap(g)
      )
  }
}
