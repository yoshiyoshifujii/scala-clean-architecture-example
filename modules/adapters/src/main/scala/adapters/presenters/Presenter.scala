package adapters.presenters


import usecases.OutputBoundary

import scala.concurrent.{ExecutionContext, Future, Promise}

trait Presenter[OutputData] extends OutputBoundary[OutputData] {

  private val promise: Promise[OutputData] = Promise[OutputData]

  override def onSuccess(result: OutputData): Unit = {
    promise.success(result)
  }

  override def onFailure(tw: Throwable): Unit = {
    promise.failure(tw)
  }

  type Response

  protected def convert(result: OutputData): Response

  def response()(implicit ec: ExecutionContext): Future[Response] =
    promise.future.map(convert)

}
