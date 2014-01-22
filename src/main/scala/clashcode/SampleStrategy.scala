package clashcode

import clashcode.robot.{RobotCode, Robot, BreedingStrategy}
import scala.util.Random

/** A simple strategy for creating a new robot from an existing generation */
object SampleStrategy extends BreedingStrategy {

  def createNewCode(currentRobots: Seq[Robot]): RobotCode = {

    // select parents
    val left = currentRobots(Random.nextInt(currentRobots.size)).code
    val right = currentRobots(Random.nextInt(currentRobots.size)).code

    // crossover
    val leftCount = Random.nextInt(left.code.length)
    val result = left.code.take(leftCount) ++ right.code.drop(leftCount)

    // TODO: mutate
    RobotCode(result)
  }

}
