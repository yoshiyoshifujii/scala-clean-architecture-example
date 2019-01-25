package adapters.presenters

import cats.data.NonEmptyList
import cats.syntax.either._
import entities.EntitiesError
import monix.eval.Task
import usecases.OutputBoundary

import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.util.{Failure, Success}

trait Presenter[OutputData] extends OutputBoundary[Task, OutputData] {

  override def onComplete(result: Task[OutputData]): Unit = {
    result
  }

}
