package usecases

trait UseCaseInteractor[M[_], InputData, OutputData] extends InputBoundary[InputData] {

  protected val outputBoundary: OutputBoundary[M, OutputData]

  override def execute(arg: InputData): Unit = outputBoundary.onComplete(dance(arg))

  protected def dance(arg: InputData): M[OutputData]

}
