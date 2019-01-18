package entities.token

import entities.client.ClientId
import entities.scope.Scopes

trait AccessTokenGenerator[F[_]] {

  def generate(scopes: Scopes, clientId: ClientId, accountId: String): F[AccessToken]

}
