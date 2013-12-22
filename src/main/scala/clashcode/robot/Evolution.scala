package clashcode.robot

import scala.util.Random
import scala.collection.parallel.ForkJoinTaskSupport


/**
 */
class Evolution(poolSize: Int, code: Option[String]) {

  var random = new Random()
  var candidates = code.map(c => CandidateCode(c.map(_.toString.toByte).toArray).evaluate).toSeq
  var candidateHashes = candidates.map(_.code.bits.toList.hashCode)

  var generation = 0
  var variability = 1.0
  var mutateCount = 3
  var firstDebug = true

  val taskSupport = new ForkJoinTaskSupport(new scala.concurrent.forkjoin.ForkJoinPool((Seq.empty[Int].par.tasksupport.parallelismLevel * 15) / 10))
  //println(taskSupport.parallelismLevel)

  tick()

  private def crossover : CandidateCode = {

    val left = candidates(random.nextInt(candidates.length)).code
    val right = candidates(random.nextInt(candidates.length)).code

    // crossover
    val leftCount = random.nextInt(left.bits.length)
    val result = left.bits.take(leftCount) ++ right.bits.drop(leftCount)

    // mutate
    do
    {
      for (i <- 0 until mutateCount) {
        result(random.nextInt(result.length)) = random.nextInt(Decisions.count).toByte
      }
    }
    while({
      // keep mutating if duplicate candidate
      val compareList = result.toList.hashCode
      val mutate = candidateHashes.contains(compareList)
      //if (mutate) println("keep mutating")
      mutate
    })

    CandidateCode(result)
  }

  def tick(count: Int) : CandidatePoints = {
    (1 until count).foreach(_ => tick())
    tick()
  }

  def tick() : CandidatePoints = {

    generation += 1

    // create next generation candidates
    val newCodes =
      (0 until poolSize - candidates.length).map(_ => Situations.getRandomCode) ++
      (0 until candidates.length).map(_ => crossover)

    // evaluate next generation
    //val newPoints = newCodes.par.map(_.evaluate)
    val par = newCodes.par
    par.tasksupport = taskSupport
    val newPoints = par.map(_.evaluate)

    // get pool of best
    val allCandidates = candidates ++ newPoints
    val bestCandidates = allCandidates.sortBy(- _.points).take(poolSize)
    candidates = bestCandidates
    candidateHashes = bestCandidates.map(_.code.bits.toList.hashCode)

    bestCandidates(0) // return best candidate
  }

  def debug() {
    //mutateCount < Situations.codeLength / 10
    //if (variability < 0.05) mutateCount += 1
    val mutResult = Math.pow(2 + (generation * Situations.codeLength) / 10000.0, -1) * 100
    println(mutResult)
    mutateCount = mutResult.toInt.max(1)
	  
    val a = candidates(0).points
    val b = candidates(1).points
    val c = candidates(2).points
    val last = candidates.last.points
    val vari = candidates.map(_.points).distinct.length / candidates.length.toDouble
    if (firstDebug) {
      val a = "a"
      val b = "b"
      val c = "c"
      val last = "last"
      val vari = "vari"
      println(f"$a%5s\t$b%5s\t$b%5s\t$c%5s\t$last%5s\t$vari%5s")
      firstDebug = false
    }
    println(f"$generation%5d\t$a%5d\t$b%5d\t$c%5d\t$last%5d\t$vari%5.3f")
  }

}
