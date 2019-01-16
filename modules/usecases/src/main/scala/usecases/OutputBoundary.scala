package usecases

import entities.ValidationResult

trait OutputBoundary[M[_], OutputData] {

  def onComplete(result: M[ValidationResult[OutputData]]): Unit

}
