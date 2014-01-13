package clashcode

import akka.actor._
import akka.pattern.{ ask, pipe }
import scala.concurrent.duration.FiniteDuration
import java.util.concurrent.TimeUnit
import scala.concurrent.ExecutionContext.Implicits._
import akka.util.Timeout
import akka.actor.ActorIdentity
import scala.collection.mutable


/**
 *
 * */
class MainActor(router: ActorRef, username: String) extends Actor {

  /** timer for interrogation */
  context.system.scheduler.schedule(FiniteDuration(1, TimeUnit.SECONDS), FiniteDuration(1, TimeUnit.SECONDS)) {
    self ! "now"
  }

  //implicit val timeout = Timeout(FiniteDuration(1, TimeUnit.SECONDS)) // needed for `?` below

  val hosts = mutable.Map.empty[String, Long]

  def receive = {

    case "now" =>
      val minTime = System.currentTimeMillis() - 3000;
      println(hosts.filter(_._2 >= minTime).keySet)
      router ! "ping"

    case "ping" =>
      sender ! ("pong", username)

    case ("pong", name: String) =>
      val key = if (name.length == 0) Seq(sender.path.address.host, sender.path.address.port).flatten.mkString(":") else name
      hosts.update(key, System.currentTimeMillis())

  }

}
