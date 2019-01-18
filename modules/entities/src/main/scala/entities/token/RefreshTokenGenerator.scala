package entities.token

trait RefreshTokenGenerator[F[_]] {

  def generate: F[RefreshToken]

}
