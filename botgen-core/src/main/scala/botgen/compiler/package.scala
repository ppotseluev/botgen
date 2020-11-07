package botgen

import botgen.bot.BotDsl
import cats.~>

package object compiler {
  type BotCompiler[F[_]] = BotDsl ~> F
}
