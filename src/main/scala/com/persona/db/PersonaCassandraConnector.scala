package com.persona.db

import com.websudos.phantom.connectors.KeySpace
import com.websudos.phantom.dsl.SimpleCassandraConnector

trait PersonaCassandraConnector extends SimpleCassandraConnector {

  implicit def keySpace: KeySpace = KeySpace("persona")

}
