package gateway.generators

trait IdGenerator[F[_], ID] {

  def generateId: F[ID]

}
