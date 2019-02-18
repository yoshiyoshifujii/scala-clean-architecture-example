package usecases

import cats.data.ReaderT
import cats.{ MonadError, StackSafeMonad }

import scala.concurrent.{ ExecutionContext, Future }
import scala.util.{ Failure, Success, Try }

object SampleErrors {

  implicit val useCaseMonadErrorForTry: MonadError[Try, UseCaseError] =
    new MonadError[Try, UseCaseError] with StackSafeMonad[Try] {
      override def pure[A](x: A): Try[A]                             = Success(x)
      override def flatMap[A, B](fa: Try[A])(f: A => Try[B]): Try[B] = fa.flatMap(f)
      override def raiseError[A](e: UseCaseError): Try[A] =
        e match {
          case UseCaseSystemError(cause)        => Failure(cause)
          case UseCaseApplicationError(message) => Failure(new Exception(message))
        }
      override def handleErrorWith[A](fa: Try[A])(f: UseCaseError => Try[A]): Try[A] =
        fa.recoverWith {
          case t => f(UseCaseSystemError(t))
        }
    }

  implicit def useCaseMonadErrorForFuture(implicit ec: ExecutionContext): MonadError[Future, UseCaseError] =
    new MonadError[Future, UseCaseError] with StackSafeMonad[Future] {
      override def pure[A](x: A): Future[A]                                   = Future.successful(x)
      override def flatMap[A, B](fa: Future[A])(f: A => Future[B]): Future[B] = fa.flatMap(f)
      override def raiseError[A](e: UseCaseError): Future[A] =
        e match {
          case UseCaseSystemError(cause)        => Future.failed(cause)
          case UseCaseApplicationError(message) => Future.failed(new Exception(message))
        }
      override def handleErrorWith[A](fa: Future[A])(f: UseCaseError => Future[A]): Future[A] =
        fa.recoverWith {
          case t => f(UseCaseSystemError(t))
        }
    }

  trait Context
  type ClientF[A] = ReaderT[Future, Context, A]

  implicit def useCaseMonadErrorForReaderT(implicit ec: ExecutionContext): MonadError[ClientF, UseCaseError] =
    new MonadError[ClientF, UseCaseError] with StackSafeMonad[ClientF] {
      override def pure[A](x: A): ClientF[A]                                     = ReaderT.pure(x)
      override def flatMap[A, B](fa: ClientF[A])(f: A => ClientF[B]): ClientF[B] = fa.flatMap(f)
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

}
