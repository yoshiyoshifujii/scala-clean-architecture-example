package entities.authorization

import java.time.ZonedDateTime

import cats.Monad
import cats.data.Validated.{ Invalid, Valid }
import cats.implicits._
import com.github.j5ik2o.dddbase.Aggregate
import entities.client.ClientId
import entities.scope.Scopes
import entities.status.Status
import entities.token._
import entities.{ EntitiesError, EntitiesValidationResult }

import scala.reflect.{ classTag, ClassTag }

case class Authorization(id: AuthorizationId,
                         clientId: ClientId,
                         scopes: Scopes,
                         accountId: String,
                         refreshToken: Option[RefreshToken],
                         status: Status,
                         createdAt: ZonedDateTime,
                         updatedAt: Option[ZonedDateTime])
    extends Aggregate {
  override type AggregateType = Authorization
  override type IdType        = AuthorizationId
  override protected val tag: ClassTag[Authorization] = classTag[Authorization]

  def generateToken[F[_]: Monad](accessTokenGenerator: AccessTokenGenerator[F],
                                 refreshTokenGenerator: RefreshTokenGenerator[F]): F[(Authorization, Token)] =
    for {
      accessToken  <- accessTokenGenerator.generate(this.scopes, this.clientId, this.accountId)
      refreshToken <- refreshTokenGenerator.generate
    } yield {
      val token = Token(
        accessToken,
        tokenType = TokenType.Bearer,
        expiresIn = ExpiresIn(3600),
        refreshToken,
        scope = this.scopes
      )
      val auth = this.copy(refreshToken = Some(token.refreshToken))
      (auth, token)
    }

  def authenticate(clientId: ClientId): EntitiesValidationResult[VerifiedAuthorization] =
    assertClientId(clientId) map { _ =>
      new VerifiedAuthorization(this)
    }

  private def assertClientId(clientId: ClientId): EntitiesValidationResult[ClientId] =
    if (clientId == this.clientId)
      clientId.validNel
    else
      EntitiesError("un match clientId").invalidNel

  class VerifiedAuthorization private[Authorization] (authorization: Authorization) {

    def generateTokenByRefreshToken[F[_]](
        accessTokenGenerator: AccessTokenGenerator[F],
        refreshToken: RefreshToken,
        scopes: Option[Seq[String]]
    )(implicit ME: Monad[F]): F[EntitiesValidationResult[Token]] =
      Scopes.fromOptSeqString(scopes) match {
        case invalid @ Invalid(_) => ME.pure(invalid)
        case Valid(a) =>
          val _scopes = a.getOrElse(this.authorization.scopes)
          for {
            accessToken <- accessTokenGenerator.generate(_scopes,
                                                         this.authorization.clientId,
                                                         this.authorization.accountId)
          } yield
            Token(
              accessToken,
              tokenType = TokenType.Bearer,
              expiresIn = ExpiresIn(3600),
              refreshToken = refreshToken,
              scope = _scopes
            ).validNel
      }

  }

}
