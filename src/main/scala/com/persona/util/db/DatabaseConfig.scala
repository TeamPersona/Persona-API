package com.persona.util.db

import slick.jdbc.JdbcBackend.{Database, Session}

trait DatabaseConfig {

  val driver = slick.driver.PostgresDriver

  def db = Database.forConfig("persona-db")

  implicit val session: Session = db.createSession()

}
