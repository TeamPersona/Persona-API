package persona.injection

import javax.inject.Inject

import com.google.inject.AbstractModule
import com.google.inject.name.Names
import play.api.{Configuration, Environment}

class PersonaModule @Inject() (
  environment: Environment,
  configuration: Configuration) extends AbstractModule {

  // Need the parenthesis here, since we're overriding a java method
  def configure() = {
    setupDataSchemas()
  }

  def setupDataSchemas() = {
    configuration.getString("persona.data.schemaDirectory") foreach { schemaDirectory =>
      bindConstant()
        .annotatedWith(Names.named("DataSchemaDirectory"))
        .to(schemaDirectory)
    }
  }

}
