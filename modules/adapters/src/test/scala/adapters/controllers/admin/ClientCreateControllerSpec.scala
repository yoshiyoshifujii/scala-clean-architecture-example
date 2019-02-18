package adapters.controllers.admin

import java.nio.charset.StandardCharsets

import adapters.presenters.Presenter.Result
import akka.http.scaladsl.model.{ ContentTypes, HttpEntity, StatusCodes }
import akka.http.scaladsl.testkit.ScalatestRouteTest
import entities.client.ClientId
import gateway.generators.{ ClientIdGeneratorMock, IdGenerator }
import gateway.repositories.{ ClientRepository, ClientRepositoryOnMemory }
import org.scalatest.FreeSpec
import usecases.UseCaseError
import wvlet.airframe._

class ClientCreateControllerSpec extends FreeSpec with ScalatestRouteTest {
  import adapters.controllers.Errors._

  private val clientIdGenerator = new ClientIdGeneratorMock[Result, UseCaseError]
  private val clientRepository  = new ClientRepositoryOnMemory[Result, UseCaseError]

  lazy val design: Design = newDesign
    .bind[IdGenerator[Result, ClientId]].toInstance(clientIdGenerator)
    .bind[ClientRepository[Result]].toInstance(clientRepository)
    .bind[ClientCreateController].toSingleton

  "ClientCreateController" - {

    "success" in design.withSession { session =>
      val dataBytes =
        s"""{
           |  "name": "test-3",
           |  "redirectUris": [
           |    "http://localhost:8082"
           |  ],
           |  "scopes": [
           |    "read-write"
           |  ]
           |}""".stripMargin.getBytes(StandardCharsets.UTF_8)

      Post(
        "/admin/clients",
        HttpEntity(ContentTypes.`application/json`, dataBytes)
      ) ~> session.build[ClientCreateController].execute ~> check {
        assert {
          status === StatusCodes.OK
        }
      }
    }

  }

}
