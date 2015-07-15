package persona.models

object User {
  // User Model Stub
  // TODO Integration with Cassandra

  val users = List(
    User("rpcrezel@uwaterloo.ca", "test123", false),
    User("m26jiang@uwaterloo.ca", "test123", false),
    User("d77kim@uwaterloo.ca", "test123", false),
    User("tstark@uwaterloo.ca", "test123", false)
  )

  def find(username:String):Option[User] = users.filter(_.username == username).headOption
}

case class User(username:String, password:String, isPartner:Boolean) {
  def checkPassword(password:String): Boolean = this.password == password;
}