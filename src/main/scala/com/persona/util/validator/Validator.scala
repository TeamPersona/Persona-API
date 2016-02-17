package persona.util.validator

trait Validator[-T] {

  def validate(value: T): Boolean

}
