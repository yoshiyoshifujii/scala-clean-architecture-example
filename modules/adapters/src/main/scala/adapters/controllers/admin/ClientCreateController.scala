package adapters.controllers.admin

import adapters.controllers.Controller
import adapters.presenters.Presenter._
import adapters.presenters.admin.ClientCreatePresenter
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import entities.client.ClientId
import gateway.generators.IdGenerator
import gateway.repositories.ClientRepository
import usecases.admin.{ ClientCreateInput, ClientCreateUseCase }

import scala.util.{ Failure, Success }

class ClientCreateController(clientIdGenerator: IdGenerator[Result, ClientId],
                             clientRepository: ClientRepository[Result])
    extends Controller {

  import adapters.controllers.Errors._
  import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
  import io.circe.generic.auto._

  case class ClientCreateInputJson(name: Option[String], redirectUris: Seq[String], scopes: Seq[String])

  private def convert(request: ClientCreateInputJson): ClientCreateInput =
    ClientCreateInput(
      name = request.name,
      redirectUris = request.redirectUris,
      scopes = request.scopes
    )

  def execute: Route =
    entity(as[ClientCreateInputJson]) { request =>
      val presenter = new ClientCreatePresenter
      val useCase   = new ClientCreateUseCase[Result](presenter, clientIdGenerator, clientRepository)
      useCase.execute(convert(request))
      onComplete(presenter.response) {
        case Failure(exception) => failWith(exception)
        case Success(value)     => value
      }
    }

}
