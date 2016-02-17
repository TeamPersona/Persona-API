package persona.util.converter

import scala.util.Try

object Converters {

  class IntConverter extends Converter[String, Int] {
    def convert(value: String): Try[Int] = Try {
      value.toInt
    }
  }

  class LongConverter extends Converter[String, Long] {
    def convert(value: String): Try[Long] = Try {
      value.toLong
    }
  }

  class StringConverter extends Converter[String, String] {
    def convert(value: String): Try[String] = Try(value)
  }

}
