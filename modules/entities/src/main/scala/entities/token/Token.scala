package entities.token

import entities.scope.Scopes

case class Token(accessToken: AccessToken,
                 tokenType: TokenType,
                 expiresIn: ExpiresIn,
                 refreshToken: RefreshToken,
                 scope: Scopes)
