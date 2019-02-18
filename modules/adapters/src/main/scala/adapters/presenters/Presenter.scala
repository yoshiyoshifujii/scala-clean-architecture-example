package adapters.presenters

import akka.actor.ActorSystem
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.StandardRoute
import cats.data.EitherT
import monix.eval.Task
import monix.execution.Scheduler
import usecases.{ OutputBoundary, UseCaseApplicationError, UseCaseError, UseCaseSystemError }
import wvlet.airframe._

import scala.concurrent.{ Future, Promise }

object Presenter {
  type Result[A] = EitherT[Task, UseCaseError, A]
}
import adapters.presenters.Presenter._

trait Presenter[OutputData] extends OutputBoundary[Result, OutputData] {

  type ViewModel = StandardRoute
  protected def convert(outputData: OutputData): ViewModel

  private lazy val actorSystem: ActorSystem     = bind[ActorSystem]
  private lazy val blockingDispatcher           = actorSystem.dispatcher // dispatchers.lookup("blocking-dispatcher")
  private lazy val blockingScheduler: Scheduler = Scheduler(blockingDispatcher)
  private val _response: Promise[ViewModel]     = Promise()

  private def badRequest(message: String): StandardRoute =
    complete(HttpResponse(BadRequest, entity = message))

  override def onComplete(result: Result[OutputData]): Unit =
    result
      .fold(
        {
          case UseCaseApplicationError(message) => badRequest(message)
          case UseCaseSystemError(cause)        => failWith(cause)
        },
        convert
      ).runAsync {
        case Left(cause)  => _response.failure(cause)
        case Right(value) => _response.success(value)
      }(blockingScheduler)

  def response: Future[ViewModel] = _response.future
}
