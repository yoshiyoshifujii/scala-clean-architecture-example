package usecases

trait UseCaseInteractor[F[_], InputData, OutputData] extends InputBoundary[InputData] {

  protected val outputBoundary: OutputBoundary[F, OutputData]

  override def execute(arg: InputData): Unit = outputBoundary.onComplete(dance(arg))

  protected def dance(arg: InputData): F[OutputData]

}
