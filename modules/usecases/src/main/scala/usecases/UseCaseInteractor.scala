package usecases

trait UseCaseInteractor[M[_], InputData, OutputData] extends InputBoundary[InputData] {

  protected val outputBoundary: OutputBoundary[M, OutputData]

  override def execute(arg: InputData): Unit = outputBoundary.onComplete(call(arg))

  protected def call(arg: InputData): M[OutputData]

}
