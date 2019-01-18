package usecases

trait OutputBoundary[F[_], OutputData] {

  def onComplete(result: F[OutputData]): Unit

}
