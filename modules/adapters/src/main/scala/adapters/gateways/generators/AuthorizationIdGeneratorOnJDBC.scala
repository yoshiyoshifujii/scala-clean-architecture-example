package adapters.gateways.generators
import entities.authorization.AuthorizationId
import gateway.generators.IdGenerator
import monix.eval.Task

class AuthorizationIdGeneratorOnJDBC extends IdGenerator[Task, AuthorizationId] {
  override def generateId: Task[AuthorizationId] = ???
}
