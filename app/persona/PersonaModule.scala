package persona

import com.google.inject.AbstractModule

// This class is used to force the creation of PersonaApplication
// This way we can register global callbacks
class PersonaModule extends AbstractModule {

  // Need the parentheses here, since we're overriding a java method
  def configure() = {
    bind(classOf[PersonaApplication])
      .to(classOf[PersonaApplicationImpl])
      .asEagerSingleton()
  }

}
