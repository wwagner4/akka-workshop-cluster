package clashcode

import clashcode.robot.{RobotCode, Robot}
import scala.util.Random

/** A simple strategy for creating a new robot from an existing generation */
case class SampleStrategy(id: String) extends MutationStrategy {
  
  def name = "Sample_" + id

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

    // TODO: mutate
    RobotCode(result, creatorName, Seq(left, right))
  }

}
