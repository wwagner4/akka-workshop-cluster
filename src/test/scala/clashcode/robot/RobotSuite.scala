package clashcode.robot

import org.scalatest.FeatureSpec
import org.scalatest.Matchers

class RobotSuite extends FeatureSpec with Matchers {

  feature("Robot") {

    scenario("should have 128 different situations") {
      128 should equal(Situations.all.size)
    }

  }
}