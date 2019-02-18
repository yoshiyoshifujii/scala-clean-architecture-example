package adapters.presenters.admin

import adapters.presenters.Presenter
import akka.http.scaladsl.server.Directives._
import usecases.admin.ClientCreateOutput

class ClientCreatePresenter extends Presenter[ClientCreateOutput] {

  import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
  import io.circe.generic.auto._

  case class ClientJson(id: String, secret: String)

  override protected def convert(outputData: ClientCreateOutput): ViewModel =
    complete(
      ClientJson(
        outputData.id.toString,
        outputData.secret
      )
    )
}
