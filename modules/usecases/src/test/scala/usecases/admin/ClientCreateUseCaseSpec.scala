package usecases.admin

import cats.data.Validated.{ Invalid, Valid }
import cats.implicits._
import entities.{ Client, ValidationResult }
import gateway.generators.ClientIdGeneratorMock
import gateway.repositories.{ ClientRepository, ClientRepositoryOnMemory }
import org.scalatest.FreeSpec
import usecases.OutputBoundary

import scala.concurrent.duration._
import scala.concurrent.{ Await, ExecutionContext, Future, Promise }
import scala.util.{ Failure, Success, Try }

class ClientCreateUseCaseSpec extends FreeSpec {

  "pattern Try" in {

    type ClientF[A] = Try[A]

    val clientRepository: ClientRepository[ClientF] =
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
      clientIdGenerator,
      clientRepository
    ).execute(input)
    assert(Await.result(output1.response, 1.second) == "1")

    val output2 = new SamplePresenter
    new ClientCreateUseCase(
      outputBoundary = output2,
      clientIdGenerator,
      clientRepository
    ).execute(input.copy(name = Some("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")))
    assert(
      Await.result(output2.response, 1.second) == "NonEmptyList(EntitiesError(name fields maximum length from 50 characters))"
    )

    val clientRepository2: ClientRepository[ClientF] =
      new ClientRepositoryOnMemory[ClientF]() {
        override def store(aggregate: Client): ClientF[Long] = Failure(new RuntimeException("hoge"))
      }
    val output3 = new SamplePresenter
    new ClientCreateUseCase(
      outputBoundary = output3,
      clientIdGenerator,
      clientRepository2
    ).execute(input)
    assertThrows[RuntimeException](Await.result(output3.response, 1.second))
  }

  "pattern Future" in {

    type ClientF[A] = Future[A]

    implicit val ec: ExecutionContext = ExecutionContext.global

    val clientRepository: ClientRepository[ClientF] =
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
      clientIdGenerator,
      clientRepository
    ).execute(input)
    assert(Await.result(output1.response, 1.second) == "1")

    val output2 = new SamplePresenter
    new ClientCreateUseCase(
      outputBoundary = output2,
      clientIdGenerator,
      clientRepository
    ).execute(input)
    assert(Await.result(output2.response, 1.second) == "2")
  }

}
