package usecases.admin

import cats.data.Validated.{Invalid, Valid}
import cats.implicits._
import entities.{Client, ClientId, ValidationResult}
import gateway.generators.ClientIdGeneratorMock
import gateway.repositories.{ClientRepository, ClientRepositoryOnMemory}
import org.scalatest.FreeSpec
import usecases.OutputBoundary

import scala.util.{Failure, Success, Try}

class ClientCreateUseCaseSpec extends FreeSpec {

  "ClientCreateUseCase" - {

    "success" in {

      type ClientF[A] = Try[A]

      implicit val clientRepository: ClientRepository[ClientF] =
        new ClientRepositoryOnMemory[ClientF]()

      val clientIdGenerator = new ClientIdGeneratorMock[ClientF]()

      val input = ClientCreateInput(
        name = Some("hoge"),
        redirectUris = Seq("http://localhost"),
        scopes = Seq("read-only")
      )

      val output = new OutputBoundary[ClientF, ClientCreateOutput] {

        private var _response: String = _

        override def onComplete(result: ClientF[ValidationResult[ClientCreateOutput]]): Unit =
          result match {
            case Success(Valid(value)) => _response = value.id.toString
            case Success(Invalid(value)) => _response = value.toString
            case Failure(cause) => _response = cause.getMessage
          }

        def response: String = _response
      }

      new ClientCreateUseCase(
        outputBoundary = output,
        clientIdGenerator = clientIdGenerator
      ).execute(input)

      assert(output.response == "1")

    }

  }

}
