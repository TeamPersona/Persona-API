package persona

import javax.inject.{Inject, Singleton}

import com.websudos.phantom.Manager
import play.api.inject.ApplicationLifecycle

import scala.concurrent.Future

trait PersonaApplication

// NOTE: Don't add to this class unless you have no other option
//       Most of the time, you can just inject the ApplicationLifecycle into your own class
@Singleton
class PersonaApplicationImpl @Inject() (lifecycle: ApplicationLifecycle) extends PersonaApplication {

  lifecycle.addStopHook { () =>
    // Shutdown Phantom
    Future.successful(Manager.shutdown())
  }

}
