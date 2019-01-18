package usecases.admin

import cats.implicits._
import gateway.repositories.{ ClientRepository, ClientRepositoryOnMemory }
import org.scalatest.FreeSpec
import usecases.OutputBoundary

import scala.concurrent.duration._
import scala.concurrent.{ Await, Future, Promise }
import scala.util.{ Failure, Success, Try }

class ClientGetsUseCaseSpec extends FreeSpec {

  "pattern Try" in {

    type ClientF[A] = Try[A]

    val clientRepository: ClientRepository[ClientF] =
      new ClientRepositoryOnMemory[ClientF]()

    val input = ClientGetsInput()

    class SamplePresenter extends OutputBoundary[ClientF, ClientGetsOutput] {
      type Response = Either[String, Seq[String]]

      private val _response: Promise[Response] = Promise()

      override def onComplete(result: ClientF[ClientGetsOutput]): Unit =
        result match {
          case Success(value) => _response.success(value.clients.map(_.toString).asRight)
          case Failure(cause) => _response.failure(cause)
        }

      def response: Future[Response] = _response.future
    }

    val output1 = new SamplePresenter
    val useCase = new ClientGetsUseCase[ClientF](
      outputBoundary = output1,
      clientRepository
    )
    useCase.execute(input)
    assert(Await.result(output1.response, 1.second).right.get.isEmpty)

  }

}
