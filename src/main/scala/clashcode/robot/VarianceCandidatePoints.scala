package clashcode.robot

/**
 * Calculates a variance value that reflects the
 * number of different bytes in codes when compairing
 * pairs of candidates
 */
class VarianceCandidatePoints {

  private val ran = new java.util.Random()
  private val sampleSize = 100

  def variance(items: Seq[CandidatePoints]): Double = {
    val opts = for (i <- 1 to sampleSize) yield {
      val i1 = ran.nextInt(items.size)
      val i2 = ran.nextInt(items.size)
      if (i1 == i2) None
      else Some(distance(items(i1), items(i2)))
    }
    val dists = opts.flatten
    dists.sum / dists.size
  }

  protected def distance(c1: CandidatePoints, c2: CandidatePoints): Double = {
    val bits1 = c1.code.bits;
    val bits2 = c2.code.bits;
    val diff = math.abs(bits1.length - bits2.length)
    val minLen = math.min(bits1.length, bits2.length)
    val diffs = for (i <- 0 until minLen) yield if (bits1(i) == bits2(i)) 0 else 1
    diffs.sum + diff
  }
}


