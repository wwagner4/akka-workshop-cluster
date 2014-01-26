package clashcode.video

import org.scalatest.FunSuite

class MaxvalSuite extends FunSuite {

  val valid = List(3, 5, 7, 9, 21, 33);
  val invalid = List(-10, -4, -3, -2, -1, 0, 1, 2, 4, 6, 8, 10, 100, 1000);

  for (v <- valid) {
    test(s"$v should be valid") {
      assert(Maxval.valid(v))
    }
  }

  for (v <- invalid) {
    test(s"$v should not be valid") {
      assert(!Maxval.valid(v))
    }
  }
}