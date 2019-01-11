package usecases

import org.scalatest.FreeSpec
import usecases.interactors.UseCaseInteractor
import usecases.outputs.OutputBoundary

import scala.concurrent.duration._
import scala.concurrent.{ Await, ExecutionContext, Future, Promise }

class ControllerSpec extends FreeSpec {

  type SampleInput  = String
  type SampleOutput = Int

  class SampleUseCase(
      override protected val outputBoundary: OutputBoundary[SampleOutput]
  ) extends UseCaseInteractor[SampleInput, SampleOutput] {

    override protected def call(arg: SampleInput)(implicit ec: ExecutionContext): Future[SampleOutput] =
      Future.apply {
        arg.toInt
      }
  }

  trait Presenter extends OutputBoundary[SampleOutput] {

    private val promise: Promise[SampleOutput] = Promise[SampleOutput]

    override def onSuccess(result: SampleOutput): Unit = {
      promise.success(result)
    }

    override def onFailure(tw: Throwable): Unit = {
      promise.failure(tw)
    }

    type Response = String
    protected def convert(result: SampleOutput): Response

    def response()(implicit ec: ExecutionContext): Future[Response] =
      promise.future.map(convert)

  }

  class SamplePresenter() extends Presenter {
    override protected def convert(result: SampleOutput): Response = {
      s"Response is $result"
    }
  }

  class SampleController(useCase: SampleUseCase, presenter: SamplePresenter) {

    def something(input: String)(implicit ex: ExecutionContext): Future[String] = {
      useCase.execute(input)
      presenter.response()
    }

  }

  "Controller" - {

    "success" in {
      implicit val ec: ExecutionContext = ExecutionContext.global

      val presenter = new SamplePresenter()

      val controller = new SampleController(
        new SampleUseCase(presenter),
        presenter
      )

      assert(Await.result(controller.something("1"), 1.second) === "Response is 1")
    }

  }

}
