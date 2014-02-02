package clashcode

import clashcode.robot.{ RobotCode, Robot }
import scala.util.Random
import clashcode.robot.Decisions

/** A simple strategy for creating a new robot from an existing generation */
case class SampleStrategy(id: String) extends Strategy {

  def name = "S_" + id

  /**
   *  Create new members of the next generation.
   *  To do so perform the following steps
   *  - Select couples of candidates to be the parents for some of the members
   *    of the next generation
   *  - create new candidates by applying crossover on the selected couples
   *  - Apply mutation on the outcome of crossing over the couples (optional)
   *  - Apply mutation on any of the candidates from the previous generation (optional)
   *  - Create new random candidates (optional)
   *
   *  currentRobots: The candidates from the current generation sorted by their fitness
   */
  def createNewCode(creatorName: String, currentRobots: Seq[Robot]): RobotCode = {

    // select parents
    val left = currentRobots(Random.nextInt(currentRobots.size)).code
    val right = currentRobots(Random.nextInt(currentRobots.size)).code

    // crossover
    val leftCount = Random.nextInt(left.code.length)
    val result = left.code.take(leftCount) ++ right.code.drop(leftCount)

    RobotCode(result, creatorName, Seq(left, right))
  }

  def receivedRobot(robot: Robot, robots: IndexedSeq[Robot]): IndexedSeq[Robot] = {
    println("Consumed a robot " + name)
    robots :+ robot
  }
}

case class MutatingStrategy(id: String, mutationCount: Int) extends Strategy {

  def name = s"M_${id}_${mutationCount}"

  def createNewCode(creatorName: String, currentRobots: Seq[Robot]): RobotCode = {

    // select parents
    val left = currentRobots(Random.nextInt(currentRobots.size)).code
    val right = currentRobots(Random.nextInt(currentRobots.size)).code

    // crossover
    val leftCount = Random.nextInt(left.code.length)
    val result = left.code.take(leftCount) ++ right.code.drop(leftCount)

    for (_ <- 1 to mutationCount) {
      val index = Random.nextInt(result.size)
      result(index) = Random.nextInt(Decisions.count).toByte;
    }

    RobotCode(result, creatorName, Seq(left, right))
  }

  def receivedRobot(robot: Robot, robots: IndexedSeq[Robot]): IndexedSeq[Robot] = {
    println("Consumed a robot " + name)
    robots :+ robot
  }
}


