package adapters.controllers

import adapters.presenters.Presenter.Result
import cats.data.EitherT
import cats.{ MonadError, StackSafeMonad }
import monix.eval.Task
import usecases.UseCaseError

object Errors {

  implicit def useCaseMonadErrorFroTask: MonadError[Result, UseCaseError] =
    new MonadError[Result, UseCaseError] with StackSafeMonad[Result] {
      override def pure[A](x: A): Result[A] = EitherT.pure(x)

      override def flatMap[A, B](fa: Result[A])(f: A => Result[B]): Result[B] = fa.flatMap(f)

      override def raiseError[A](e: UseCaseError): Result[A] = EitherT.leftT[Task, A](e)

      override def handleErrorWith[A](fa: Result[A])(f: UseCaseError => Result[A]): Result[A] =
        fa.recoverWith {
          case t => f(t)
        }
    }

}
