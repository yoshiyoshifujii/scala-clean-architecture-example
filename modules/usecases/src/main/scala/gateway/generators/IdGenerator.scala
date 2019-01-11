package gateway.generators

trait IdGenerator[M[_], ID] {

  def generateId: M[ID]

}
