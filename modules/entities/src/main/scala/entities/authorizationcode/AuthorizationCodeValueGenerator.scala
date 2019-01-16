package entities.authorizationcode

import infrastructure.token.TokenGenerator

object AuthorizationCodeValueGenerator {

  def generate: AuthorizationCodeValue = AuthorizationCodeValue(TokenGenerator.generateTokenWithSizeOf(64))

}
