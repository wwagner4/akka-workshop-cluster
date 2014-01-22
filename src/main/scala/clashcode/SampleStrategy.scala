package clashcode

import clashcode.robot.{CandidateCode, CandidatePoints, BreedingStrategy}
import scala.util.Random

/** A simple strategy for creating a new robot from an existing generation */
object SampleStrategy extends BreedingStrategy {

  def createNewCode(currentRobots: Seq[CandidatePoints]): CandidateCode = {

    // select parents
    val left = currentRobots(Random.nextInt(currentRobots.size)).code
    val right = currentRobots(Random.nextInt(currentRobots.size)).code

    // crossover
    val leftCount = Random.nextInt(left.bits.length)
    val result = left.bits.take(leftCount) ++ right.bits.drop(leftCount)

    // TODO: mutate
    CandidateCode(result)
  }

}
