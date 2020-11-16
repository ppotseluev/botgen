package botgen.api

import java.io.FileInputStream
import java.security.KeyStore

import cats.effect.{ConcurrentEffect, ContextShift, ExitCode, Resource, Sync, Timer}
import cats.syntax.functor._
import javax.net.ssl.{KeyManagerFactory, SSLContext}
import org.http4s.HttpApp
import org.http4s.server.blaze.BlazeServerBuilder
import cats.syntax.flatMap._

import scala.concurrent.ExecutionContext

class ApiApp[F[_] : ConcurrentEffect](apiConfig: ApiConfig)
                                     (implicit cs: ContextShift[F],
                                      ec: ExecutionContext,
                                      timer: Timer[F]) {

  private val apiComponents: ApiComponents[F] = new ApiComponents(apiConfig)

  private val httpAppResource: Resource[F, HttpApp[F]] = apiComponents
    .endpointsImplementation
    .map(new ApiRoutes(_).buildHttpApp)

  private val initSslContext: F[SSLContext] = Sync[F].delay {
    import apiConfig.sslConfig._
    val sslContext = SSLContext.getInstance("TLS")
    val keyStore = KeyStore.getInstance("JKS")
    val fin: FileInputStream = new FileInputStream(keyStorePath)
    keyStore.load(fin, storePassword.toCharArray)
    val kmf = KeyManagerFactory.getInstance("SunX509")
    kmf.init(keyStore, keyPassword.toCharArray)
    sslContext.init(kmf.getKeyManagers, null, null)
    sslContext
  }

  val runServer: F[ExitCode] = httpAppResource.use { httpApp =>
    initSslContext.flatMap(runServer(httpApp))
  }

  private def runServer(httpApp: HttpApp[F])
                       (sslContext: SSLContext): F[ExitCode] =
    BlazeServerBuilder[F](ec)
      .bindHttp(apiConfig.apiPort, "0.0.0.0")
      .withHttpApp(httpApp)
      .withSslContext(sslContext)
      .serve
      .compile
      .drain
      .as(ExitCode.Success)
}
