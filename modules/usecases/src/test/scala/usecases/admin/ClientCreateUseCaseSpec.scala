package usecases.admin

import cats._
import cats.data.ReaderT
import cats.implicits._
import entities.client.Client
import gateway.generators.ClientIdGeneratorMock
import gateway.repositories.{ ClientRepository, ClientRepositoryOnMemory }
import org.scalatest.FreeSpec
import usecases.{ OutputBoundary, UseCaseApplicationError, UseCaseError, UseCaseSystemError }

import scala.concurrent.duration._
import scala.concurrent.{ Await, ExecutionContext, Future, Promise }
import scala.util.{ Failure, Success, Try }

class ClientCreateUseCaseSpec extends FreeSpec {

  "pattern Try" - {

    type ClientF[A] = Try[A]

    val clientRepository: ClientRepository[ClientF] =
      new ClientRepositoryOnMemory[ClientF]()

    val clientIdGenerator = new ClientIdGeneratorMock[ClientF]()

    val input = ClientCreateInput(
      name = Some("hoge"),
      redirectUris = Seq("http://localhost"),
      scopes = Seq("read-only")
    )

    class SamplePresenter extends OutputBoundary[ClientF, ClientCreateOutput] {

      private val _response: Promise[String] = Promise()

      override def onComplete(result: ClientF[ClientCreateOutput]): Unit =
        result match {
          case Success(value) => _response.success(value.id.toString)
          case Failure(cause) => _response.failure(cause)
        }

      def response: Future[String] = _response.future
    }

    implicit val monadError: MonadError[ClientF, UseCaseError] =
      new MonadError[ClientF, UseCaseError] with StackSafeMonad[ClientF] {
        override def pure[A](x: A): ClientF[A] =
          Success(x)
        override def flatMap[A, B](fa: ClientF[A])(f: A => ClientF[B]): ClientF[B] =
          fa.flatMap(f)
        override def raiseError[A](e: UseCaseError): ClientF[A] =
          e match {
            case UseCaseSystemError(cause)        => Failure(cause)
            case UseCaseApplicationError(message) => Failure(new Exception(message))
          }
        override def handleErrorWith[A](fa: ClientF[A])(f: UseCaseError => ClientF[A]): ClientF[A] =
          fa.recoverWith {
            case t => f(UseCaseSystemError(t))
          }
      }

    "success" in {
      val output = new SamplePresenter
      new ClientCreateUseCase(
        outputBoundary = output,
        clientIdGenerator,
        clientRepository
      ).execute(input)
      assert(Await.result(output.response, 1.second) == "1")
    }

    "name length over maximum" in {
      val output = new SamplePresenter
      new ClientCreateUseCase(
        outputBoundary = output,
        clientIdGenerator,
        clientRepository
      ).execute(input.copy(name = Some("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")))
      assertThrows[Exception](
        Await.result(output.response, 1.second)
      )
    }

    "throw exception" in {
      val clientRepository2: ClientRepository[ClientF] =
        new ClientRepositoryOnMemory[ClientF]() {
          override def store(aggregate: Client): ClientF[Long] = Failure(new RuntimeException("hoge"))
        }
      val output = new SamplePresenter
      new ClientCreateUseCase(
        outputBoundary = output,
        clientIdGenerator,
        clientRepository2
      ).execute(input)
      assertThrows[RuntimeException](Await.result(output.response, 1.second))
    }

    "redirectUris is empty" in {
      val output = new SamplePresenter
      new ClientCreateUseCase(
        outputBoundary = output,
        clientIdGenerator,
        clientRepository
      ).execute(input.copy(redirectUris = Seq.empty))
      assertThrows[Exception](
        Await.result(output.response, 1.second)
      )
    }

    "scopes is empty" in {
      val output = new SamplePresenter
      new ClientCreateUseCase(
        outputBoundary = output,
        clientIdGenerator,
        clientRepository
      ).execute(input.copy(scopes = Seq.empty))
      assertThrows[Exception](
        Await.result(output.response, 1.second)
      )
    }

    "scope invalid value" in {
      val output = new SamplePresenter
      new ClientCreateUseCase(
        outputBoundary = output,
        clientIdGenerator,
        clientRepository
      ).execute(input.copy(scopes = Seq("hoge", "fuga")))
      assertThrows[Exception](
        Await.result(output.response, 1.second)
      )
    }
  }

  "pattern Future" in {

    type ClientF[A] = Future[A]

    implicit val ec: ExecutionContext = ExecutionContext.global

    val clientRepository: ClientRepository[ClientF] =
      new ClientRepositoryOnMemory[ClientF]()

    val clientIdGenerator = new ClientIdGeneratorMock[ClientF]()

    val input = ClientCreateInput(
      name = Some("hoge"),
      redirectUris = Seq("http://localhost"),
      scopes = Seq("read-only")
    )

    class SamplePresenter extends OutputBoundary[ClientF, ClientCreateOutput] {

      private val _response: Promise[String] = Promise()

      override def onComplete(result: ClientF[ClientCreateOutput]): Unit =
        result.onComplete({
          case Success(value) => _response.success(value.id.toString)
          case Failure(cause) => _response.failure(cause)
        })

      def response: Future[String] = _response.future
    }

    implicit val monadError: MonadError[ClientF, UseCaseError] =
      new MonadError[ClientF, UseCaseError] with StackSafeMonad[ClientF] {
        override def pure[A](x: A): ClientF[A] =
          Future.successful(x)
        override def flatMap[A, B](fa: ClientF[A])(f: A => ClientF[B]): ClientF[B] =
          fa.flatMap(f)
        override def raiseError[A](e: UseCaseError): ClientF[A] =
          e match {
            case UseCaseSystemError(cause)        => Future.failed(cause)
            case UseCaseApplicationError(message) => Future.failed(new Exception(message))
          }
        override def handleErrorWith[A](fa: ClientF[A])(f: UseCaseError => ClientF[A]): ClientF[A] =
          fa.recoverWith {
            case t => f(UseCaseSystemError(t))
          }
      }

    val output1 = new SamplePresenter
    new ClientCreateUseCase(
      outputBoundary = output1,
      clientIdGenerator,
      clientRepository
    ).execute(input)
    assert(Await.result(output1.response, 1.second) == "1")

    val output2 = new SamplePresenter
    new ClientCreateUseCase(
      outputBoundary = output2,
      clientIdGenerator,
      clientRepository
    ).execute(input)
    assert(Await.result(output2.response, 1.second) == "2")
  }

  "pattern ReaderT" in {

    class Context

    type ClientF[A] = ReaderT[Future, Context, A]

    implicit val ec: ExecutionContext = ExecutionContext.global

    val clientRepository: ClientRepository[ClientF] =
      new ClientRepositoryOnMemory[ClientF]()

    val clientIdGenerator = new ClientIdGeneratorMock[ClientF]()

    val input = ClientCreateInput(
      name = Some("hoge"),
      redirectUris = Seq("http://localhost"),
      scopes = Seq("read-only")
    )

    class SamplePresenter(context: Context) extends OutputBoundary[ClientF, ClientCreateOutput] {

      private val _response: Promise[String] = Promise()

      override def onComplete(result: ClientF[ClientCreateOutput]): Unit =
        result
          .run(context).onComplete({
            case Success(value) => _response.success(value.id.toString)
            case Failure(cause) => _response.failure(cause)
          })

      def response: Future[String] = _response.future
    }

    implicit val monadError: MonadError[ClientF, UseCaseError] =
      new MonadError[ClientF, UseCaseError] with StackSafeMonad[ClientF] {
        override def pure[A](x: A): ClientF[A] =
          ReaderT.pure(x)
        override def flatMap[A, B](fa: ClientF[A])(f: A => ClientF[B]): ClientF[B] =
          fa.flatMap(f)
        override def raiseError[A](e: UseCaseError): ClientF[A] =
          e match {
            case UseCaseSystemError(cause) => ReaderT[Future, Context, A](_ => Future.failed(cause))
            case UseCaseApplicationError(message) =>
              ReaderT[Future, Context, A](_ => Future.failed(new Exception(message)))
          }
        override def handleErrorWith[A](fa: ClientF[A])(f: UseCaseError => ClientF[A]): ClientF[A] =
          ReaderT[Future, Context, A] { a: Context =>
            fa.run(a).recoverWith {
              case t => f(UseCaseSystemError(t)).run(a)
            }
          }
      }

    val output1 = new SamplePresenter(new Context)
    new ClientCreateUseCase(
      outputBoundary = output1,
      clientIdGenerator,
      clientRepository
    ).execute(input)
    assert(Await.result(output1.response, 1.second) == "1")

    val output2 = new SamplePresenter(new Context)
    new ClientCreateUseCase(
      outputBoundary = output2,
      clientIdGenerator,
      clientRepository
    ).execute(input)
    assert(Await.result(output2.response, 1.second) == "2")
  }

}
