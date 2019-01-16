package entities.token

trait RefreshTokenGenerator[M[_]] {

  def generate: M[RefreshToken]

}
