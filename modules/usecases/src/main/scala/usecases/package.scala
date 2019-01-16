package object usecases {

  case class UseCaseError(message: String)

  type UseCaseValidationResult[A] = Either[UseCaseError, A]

}
