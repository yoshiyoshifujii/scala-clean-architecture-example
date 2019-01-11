package gateway

import cats.data.ReaderT

import scala.concurrent.Future

trait Context[S] {
  val session: S
}

object Context {
  type Reader[A] = ReaderT[Future, Context, A]
}
