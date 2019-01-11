package usecases.outputs

trait OutputBoundary[OutputData] {

  def onSuccess(result: OutputData): Unit

  def onFailure(tw: Throwable): Unit

}
