package usecases.authorization

import cats.implicits._
import entities.client.{ Client, ClientId }
import entities.secret.Secret
import gateway.generators.AuthorizationIdGeneratorMock
import gateway.repositories.{
  AuthorizationCodeRepositoryOnMemory,
  AuthorizationRepositoryOnMemory,
  ClientRepositoryOnMemory,
  ReservedAuthorizationRepositoryOnMemory
}
import org.scalatest.FreeSpec
import usecases.OutputBoundary

import scala.concurrent.duration._
import scala.concurrent.{ Await, Future, Promise }
import scala.util.{ Failure, Success, Try }

class AuthorizationUseCaseSpec extends FreeSpec {

  import usecases.Errors._

  "pattern Try" - {

    "success" in {

      type AuthF[A] = Try[A]

      val clientRepository                = new ClientRepositoryOnMemory[AuthF]
      val reservedAuthorizationRepository = new ReservedAuthorizationRepositoryOnMemory[AuthF]
      val authorizationIdGenerator        = new AuthorizationIdGeneratorMock[AuthF]
      val authorizationCodeRepository     = new AuthorizationCodeRepositoryOnMemory[AuthF]
      val authorizationRepository         = new AuthorizationRepositoryOnMemory[AuthF]

      class PresenterSpec[A] extends OutputBoundary[AuthF, A] {
        private val _response: Promise[A] = Promise()

        override def onComplete(result: AuthF[A]): Unit =
          result match {
            case Success(value) => _response.success(value)
            case Failure(cause) => _response.failure(cause)
          }
        def response: Future[A] = _response.future
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

      val output = new PresenterSpec[AuthorizationAuthorizeOutput]
      new AuthorizationAuthorizeUseCase(
        outputBoundary = output,
        clientRepository,
        reservedAuthorizationRepository
      ).execute(
        AuthorizationAuthorizeInput(
          responseType = "code",
          clientId = 1L,
          redirectUri = Some("http://localhost"),
          scope = Some(Seq("read-only")),
          state = None
        )
      )

      val authorizeOutput = Await.result(output.response, 1.second)
      assert {
        authorizeOutput.clientId == 1L
      }

      // approve
      val output2 = new PresenterSpec[AuthorizationApproveOutput]
      new AuthorizationApproveUseCase(
        outputBoundary = output2,
        authorizationIdGenerator,
        reservedAuthorizationRepository,
        authorizationCodeRepository,
        authorizationRepository
      ).execute(
        AuthorizationApproveInput(
          refKey = authorizeOutput.refKey.get,
          clientId = 1L,
          scope = Some(Seq("read-only")),
          accountId = "acid1"
        )
      )
      val approveOutput = Await.result(output2.response, 1.second)
      assert {
        approveOutput.accountId.get == "acid1"
      }
    }

  }

}
