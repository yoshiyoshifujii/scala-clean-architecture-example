package usecases.inputs

import scala.concurrent.ExecutionContext

trait InputBoundary[InputData] {

  def execute(arg: InputData)(implicit ec: ExecutionContext): Unit

}
