package clashcode

import akka.actor._
import akka.pattern.{ ask, pipe }
import scala.concurrent.duration.FiniteDuration
import java.util.concurrent.TimeUnit
import scala.concurrent.ExecutionContext.Implicits._
import akka.util.Timeout
import akka.actor.ActorIdentity


/**
 *
 * */
class MainActor(router: ActorRef, port: Int) extends Actor {

  /** timer for interrogation */
  context.system.scheduler.schedule(FiniteDuration(1, TimeUnit.SECONDS), FiniteDuration(1, TimeUnit.SECONDS)) {
    self ! "now"
  }

  implicit val timeout = Timeout(FiniteDuration(1, TimeUnit.SECONDS)) // needed for `?` below

  def receive = {
    case "now" =>

      //val identifyRequest = (router ? Identify("x")).mapTo[ActorIdentity]
      //identifyRequest.map(response => println("Identity: " + response))
      router ! "ping"

    case "ping" => sender ! ("pong " + port)
    case "pong" => println("pong")
    case x => println(x)
  }

}
