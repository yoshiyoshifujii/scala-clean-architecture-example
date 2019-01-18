package entities.secret

import org.passay.{ CharacterRule, EnglishCharacterData, PasswordGenerator }

import scala.collection.JavaConverters._

object SecretGenerator {

  private val generator = new PasswordGenerator

  private val toCharacterRules: Seq[CharacterRule] = Seq(
    new CharacterRule(EnglishCharacterData.UpperCase, 2),
    new CharacterRule(EnglishCharacterData.LowerCase, 2),
    new CharacterRule(EnglishCharacterData.Digit, 1),
    new CharacterRule(
      new org.passay.CharacterData() {
        override def getErrorCode: String = "ERR_CODE"

        override def getCharacters: String = "!@#$%&*()_+-=[]|,./?><"
      },
      1
    )
  )

  def generate: Secret = Secret(generator.generatePassword(8, toCharacterRules.asJava))

}
