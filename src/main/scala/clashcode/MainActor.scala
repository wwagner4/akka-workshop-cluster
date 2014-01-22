package clashcode

import akka.actor._
import scala.concurrent.duration.FiniteDuration
import java.util.concurrent.TimeUnit
import scala.concurrent.ExecutionContext.Implicits._
import scala.collection.mutable


class MainActor(router: ActorRef, username: String) extends Actor {

  /** timed actions */
  context.system.scheduler.schedule(FiniteDuration(10, TimeUnit.SECONDS), FiniteDuration(10, TimeUnit.SECONDS)) {
    self ! "every-10-seconds"
  }

  def receive = {

    case "every-10-seconds" =>
      router ! "hello"

  }

}
