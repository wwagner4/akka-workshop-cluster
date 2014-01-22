package clashcode.robot

import scala.util.Random



/** the code that represents the decisions of the robot in all situations */
case class CandidateCode(bits: Array[Byte]) {
  if (bits.length != Situations.codeLength) throw new IllegalArgumentException("Length of bits must be " + Situations.codeLength)

  /** evaluate this code */
  def evaluate : CandidatePoints = {
    val decisions = toDecisions
    val points = Evaluator.evaluate(decisions)
    CandidatePoints(this, points)
  }

  /** get decisions from this code */
  private def toDecisions : IndexedSeq[Decision] = {
    bits.map(x => Decisions.all(x))
  }
}

case class CandidatePoints(code: CandidateCode, points: Int)

