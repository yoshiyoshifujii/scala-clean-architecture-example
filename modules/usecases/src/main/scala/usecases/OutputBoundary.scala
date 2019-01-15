package usecases

import entities.ValidationResult

import scala.language.higherKinds

trait OutputBoundary[M[_], OutputData] {

  def onComplete(result: M[ValidationResult[OutputData]]): Unit

}
