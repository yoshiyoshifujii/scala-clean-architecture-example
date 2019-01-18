package adapters.presenters

import cats.data.NonEmptyList
import cats.syntax.either._
import entities.EntitiesError
import usecases.OutputBoundary

import scala.concurrent.{ ExecutionContext, Future, Promise }
import scala.util.{ Failure, Success }

trait Presenter[OutputData] extends OutputBoundary[Future, OutputData] {

  implicit val executionContext: ExecutionContext

  private val promise: Promise[OutputData] = Promise[OutputData]

  override def onComplete(result: Future[OutputData]): Unit =
    result.onComplete({
      case Failure(cause) => promise.failure(cause)
      case Success(value) => promise.success(value)
    })

  type ViewModel
  type ErrorResponse
  type Response = Either[ErrorResponse, ViewModel]

  protected def convert(result: OutputData): ViewModel
  protected def convertError(result: NonEmptyList[EntitiesError]): ErrorResponse

  def response(): Future[Response] =
    promise.future.map { value =>
      convert(value).asRight
    }

}
