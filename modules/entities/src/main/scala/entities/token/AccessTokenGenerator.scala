package entities.token

import entities.client.ClientId
import entities.scope.Scopes

trait AccessTokenGenerator[M[_]] {

  def generate(scopes: Scopes, clientId: ClientId, accountId: String): M[AccessToken]

}
