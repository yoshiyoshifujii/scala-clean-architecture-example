package infrastructure.token

import scala.util.Random
import org.apache.commons.codec.binary.Hex

object TokenGenerator {

  private val random = new Random(new java.security.SecureRandom())

  def generateTokenWithSizeOf(sizeInByte: Int): String = {
    val bytes = Array.ofDim[Byte](sizeInByte)
    random.nextBytes(bytes)
    new String(Hex.encodeHex(bytes))
  }

}
