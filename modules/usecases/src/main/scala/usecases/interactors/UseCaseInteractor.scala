package usecases.interactors

import usecases.inputs.InputBoundary
import usecases.outputs.OutputBoundary

import scala.concurrent.{ ExecutionContext, Future }
import scala.util.{ Failure, Success }

trait UseCaseInteractor[InputData, OutputData] extends InputBoundary[InputData] {

  protected val outputBoundary: OutputBoundary[OutputData]

  override def execute(arg: InputData)(implicit ec: ExecutionContext): Unit =
    call(arg).onComplete {
      case Success(result) =>
        outputBoundary.onSuccess(result)
      case Failure(tw) =>
        outputBoundary.onFailure(tw)
    }

  protected def call(arg: InputData)(implicit ec: ExecutionContext): Future[OutputData]

}
