package clashcode

import clashcode.robot.CandidatePoints
import clashcode.robot.CandidateVariance

object Debug {

  var firstDebug = true
  val candVari = new CandidateVariance()
  val sepa = "\t"

  def print(generation: Int, candidates: Seq[CandidatePoints]) = {

    if (firstDebug) {
      val gen = "gen"
      val first = "first"
      val last = "last"
      val vari = "vari"
      val vari1 = "vari1"
      println(f"$gen%5s$sepa$first%5s$sepa$last%5s$sepa$vari%5s$sepa$vari1%5s")
      firstDebug = false
    }

    val first = candidates(0).points
    val last = candidates.last.points
    val vari = candidates.map(_.points).distinct.length / candidates.length.toDouble
    val vari1 = candVari.diffCount(candidates)
    println(f"$generation%5d$sepa$first%5d$sepa$last%5d$sepa$vari%5.3f$sepa$vari1%5.3f")
  }

}
