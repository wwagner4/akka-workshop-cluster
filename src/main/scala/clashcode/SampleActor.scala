package clashcode

import akka.actor._
import scala.concurrent.duration.FiniteDuration
import java.util.concurrent.TimeUnit
import scala.concurrent.ExecutionContext.Implicits._
import clashcode.robot._
import scala.util.Random

case object Every10Seconds
case object Evolve

class SampleActor(broadcast: ActorRef) extends Actor {

  val myName = "anonymous" // created robots should be tagged with this name

  // these are my robots!
  var robots = (1 to 100).map(_ => RobotCode.createRandomCode(myName).evaluate)

  // send message to myself every 10 seconds
  context.system.scheduler.schedule(FiniteDuration(10, TimeUnit.SECONDS), FiniteDuration(10, TimeUnit.SECONDS)) {
    self ! Every10Seconds
  }

  def receive = {

    case Every10Seconds =>
      broadcast ! Random.shuffle(robots).head // broadcast random robot of mine (every 10 seconds)

    case Evolve =>
      evolve()
      self ! Evolve // constantly evolve

    case robot: Robot =>
      println("I received a robot from someone! what should I do?")

  }

  def evolve() = {

    // create next generation robots
    val newRobotCodes = for (i <- 1 to 100) yield SampleStrategy.createNewCode(myName, robots)

    // evaluate those new robots (using multiple cores)
    val newRobots = newRobotCodes.par.map(_.evaluate)

    // keep only the best robots
    val allRobots = robots ++ newRobots
    robots = allRobots.sortBy(-_.points).take(robots.size)

    DebugHelper.print(robots) // print some info about our generation
  }

}


