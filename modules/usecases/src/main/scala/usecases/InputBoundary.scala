package usecases

trait InputBoundary[InputData] {

  def execute(arg: InputData): Unit

}
