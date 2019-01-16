package usecases

import entities.EntitiesValidationResult

trait OutputBoundary[M[_], OutputData] {

  def onComplete(result: M[EntitiesValidationResult[OutputData]]): Unit

}
