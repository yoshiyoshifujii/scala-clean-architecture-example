package adapters.presenters

import cats.data.NonEmptyList
import cats.data.Validated.{ Invalid, Valid }
import cats.syntax.either._
import entities.{ EntitiesError, EntitiesValidationResult }
import usecases.OutputBoundary

import scala.concurrent.{ ExecutionContext, Future, Promise }
import scala.util.{ Failure, Success }

trait Presenter[OutputData] extends OutputBoundary[Future, OutputData] {

  implicit val executionContext: ExecutionContext

  private val promise: Promise[EntitiesValidationResult[OutputData]] = Promise[EntitiesValidationResult[OutputData]]

  override def onComplete(result: Future[EntitiesValidationResult[OutputData]]): Unit =
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
    promise.future.map {
      case Valid(value) => convert(value).asRight
      case Invalid(ne)  => convertError(ne).asLeft
    }

}
