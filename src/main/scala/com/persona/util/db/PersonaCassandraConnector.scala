package com.persona.util.db

import com.websudos.phantom.connectors.{ContactPoint, KeySpace}

trait KeySpaceDefinition {

  implicit val keySpace = KeySpace("persona")

}

object Defaults extends KeySpaceDefinition {

  val connector = ContactPoint.local.keySpace(keySpace.name)

}

trait PersonaCassandraConnector extends Defaults.connector.Connector
