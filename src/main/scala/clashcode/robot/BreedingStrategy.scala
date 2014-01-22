package clashcode.robot


/**
 * Defines how members of the next generation are created
 */
trait BreedingStrategy {

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

  def createNewCode(currentRobots: Seq[CandidatePoints]): CandidateCode

}

