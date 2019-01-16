package entities.refkey

import infrastructure.token.TokenGenerator

object RefKeyGenerator {

  def generate: RefKey = RefKey(TokenGenerator.generateTokenWithSizeOf(32))

}
