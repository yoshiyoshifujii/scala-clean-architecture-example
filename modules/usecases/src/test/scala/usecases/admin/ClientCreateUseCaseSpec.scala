package usecases.admin

import cats.data.Validated.{ Invalid, Valid }
import cats.implicits._
import entities.ValidationResult
import gateway.generators.ClientIdGeneratorMock
import gateway.repositories.{ ClientRepository, ClientRepositoryOnMemory }
import org.scalatest.FreeSpec
import usecases.OutputBoundary

import scala.concurrent.duration._
import scala.concurrent.{ Await, ExecutionContext, Future, Promise }
import scala.util.{ Failure, Success, Try }

class ClientCreateUseCaseSpec extends FreeSpec {

  "ClientCreateUseCase" - {

    "pattern Try" in {

      type ClientF[A] = Try[A]

      implicit val clientRepository: ClientRepository[ClientF] =
        new ClientRepositoryOnMemory[ClientF]()

      val clientIdGenerator = new ClientIdGeneratorMock[ClientF]()

      val input = ClientCreateInput(
        name = Some("hoge"),
        redirectUris = Seq("http://localhost"),
        scopes = Seq("read-only")
      )

      class SamplePresenter extends OutputBoundary[ClientF, ClientCreateOutput] {

        private val _response: Promise[String] = Promise()

        override def onComplete(result: ClientF[ValidationResult[ClientCreateOutput]]): Unit =
          result match {
            case Success(Valid(value))   => _response.success(value.id.toString)
            case Success(Invalid(value)) => _response.success(value.toString)
            case Failure(cause)          => _response.failure(cause)
          }

        def response: Future[String] = _response.future
      }

      val output1 = new SamplePresenter
      new ClientCreateUseCase(
        outputBoundary = output1,
        clientIdGenerator = clientIdGenerator
      ).execute(input)
      assert(Await.result(output1.response, 1.second) == "1")

      val output2 = new SamplePresenter
      new ClientCreateUseCase(
        outputBoundary = output2,
        clientIdGenerator = clientIdGenerator
      ).execute(input)
      assert(Await.result(output2.response, 1.second) == "2")
    }

    "pattern Future" in {

      type ClientF[A] = Future[A]

      implicit val ec: ExecutionContext = ExecutionContext.global

      implicit val clientRepository: ClientRepository[ClientF] =
        new ClientRepositoryOnMemory[ClientF]()

      val clientIdGenerator = new ClientIdGeneratorMock[ClientF]()

      val input = ClientCreateInput(
        name = Some("hoge"),
        redirectUris = Seq("http://localhost"),
        scopes = Seq("read-only")
      )

      class SamplePresenter extends OutputBoundary[ClientF, ClientCreateOutput] {

        private val _response: Promise[String] = Promise()

        override def onComplete(result: ClientF[ValidationResult[ClientCreateOutput]]): Unit =
          result.onComplete({
            case Success(Valid(value))   => _response.success(value.id.toString)
            case Success(Invalid(value)) => _response.success(value.toString)
            case Failure(cause)          => _response.failure(cause)
          })

        def response: Future[String] = _response.future
      }

      val output1 = new SamplePresenter
      new ClientCreateUseCase(
        outputBoundary = output1,
        clientIdGenerator = clientIdGenerator
      ).execute(input)
      assert(Await.result(output1.response, 1.second) == "1")

      val output2 = new SamplePresenter
      new ClientCreateUseCase(
        outputBoundary = output2,
        clientIdGenerator = clientIdGenerator
      ).execute(input)
      assert(Await.result(output2.response, 1.second) == "2")
    }

  }

}
