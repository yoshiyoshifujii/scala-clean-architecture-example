package usecases

trait OutputBoundary[M[_], OutputData] {

  def onComplete(result: M[OutputData]): Unit

}
