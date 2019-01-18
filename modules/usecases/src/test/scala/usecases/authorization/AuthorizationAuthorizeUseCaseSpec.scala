package usecases.authorization

import cats.implicits._
import entities.client.{ Client, ClientId }
import entities.secret.Secret
import gateway.repositories.{ ClientRepositoryOnMemory, ReservedAuthorizationRepositoryOnMemory }
import org.scalatest.FreeSpec
import usecases.OutputBoundary

import scala.concurrent.duration._
import scala.concurrent.{ Await, Future, Promise }
import scala.util.{ Failure, Success, Try }

class AuthorizationAuthorizeUseCaseSpec extends FreeSpec {

  import usecases.Errors._

  "pattern Try" - {

    "success" in {

      type AuthF[A] = Try[A]

      val clientRepository                = new ClientRepositoryOnMemory[AuthF]()
      val reservedAuthorizationRepository = new ReservedAuthorizationRepositoryOnMemory[AuthF]()

      class SamplePresenter extends OutputBoundary[AuthF, AuthorizationAuthorizeOutput] {

        private val _response: Promise[String] = Promise()

        override def onComplete(result: AuthF[AuthorizationAuthorizeOutput]): Unit =
          result match {
            case Success(value) => _response.success(value.clientId.toString)
            case Failure(cause) => _response.failure(cause)
          }

        def response: Future[String] = _response.future
      }

      clientRepository
        .store(
          Client
            .create(
              id = ClientId(1L),
              name = Some("hoge"),
              secret = Secret("secret"),
              redirectUris = Seq("http://localhost"),
              scopes = Seq("read-only")
            ).getOrElse(throw new RuntimeException(""))
        ).get

      val input = AuthorizationAuthorizeInput(
        responseType = "code",
        clientId = 1L,
        redirectUri = Some("http://localhost"),
        scope = Some(Seq("read-only")),
        state = None
      )
      val output = new SamplePresenter
      new AuthorizationAuthorizeUseCase(
        outputBoundary = output,
        clientRepository,
        reservedAuthorizationRepository
      ).execute(input)
      assert {
        Await.result(output.response, 1.second) == "1"
      }

    }

  }

}
