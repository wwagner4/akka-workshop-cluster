package clashcode

import org.scalatest.FunSuite
import clashcode.robot.Evaluator
import clashcode.robot.Converter
import clashcode.robot.FieldPos
import scala.util.Random
import clashcode.robot.FieldFactory
import clashcode.robot.FieldEvaluator
import clashcode.video._

class SceneCreatorSuite extends FunSuite {

  test("Create path from result with original Evaluator") {
    def strCodeToPath(strCode: String): List[FieldPos] = {
      val code: Array[Byte] = strCode.map(c => (c - 48).toByte).toArray
      val decisions = Converter.toDecisions(code)
      Evaluator.evaluate(decisions).path
    }
    val strCode = "03530311022335213110315511111120251141140200400110522540004423424544141444444444142541204404414145445445424454151340002434334143"
    val path = strCodeToPath(strCode)
    assert(path.length === 40000)
    assert(path.take(10) === List(FieldPos(1, 0), FieldPos(2, 0), FieldPos(2, 0), FieldPos(3, 0), FieldPos(3, 0), FieldPos(4, 0), FieldPos(5, 0), FieldPos(5, 0), FieldPos(6, 0), FieldPos(7, 0)))
  }

  test("Create path from result with PathUtil") {
    val strCode = "03530311022335213110315511111120251141140200400110522540004423424544141444444444142541204404414145445445424454151340002434334143"
    val path = PathUtil.strCodeToPath(strCode, 200)
    assert(path.length === 200)
    assert(path.take(10) === List(FieldPos(4, 0), FieldPos(4, 1), FieldPos(4, 1), FieldPos(5, 1), FieldPos(5, 1), FieldPos(5, 2), FieldPos(5, 3), FieldPos(5, 3), FieldPos(4, 3), FieldPos(4, 4)))
  }

  test("Split path to steps") {
    val strCode = "03530311022335213110315511111120251141140200400110522540004423424544141444444444142541204404414145445445424454151340002434334143"
    val path = PathUtil.strCodeToPath(strCode, 200).take(4)
    val steps: List[FieldStep] = PathUtil.pathToSteps(path)
    val expectedSteps = List(
      FieldStep(FieldPos(4, 0), FieldPos(4, 1)),
      FieldStep(FieldPos(4, 1), FieldPos(4, 1)),
      FieldStep(FieldPos(4, 1), FieldPos(5, 1)))
    assert(steps === expectedSteps)
  }

  val dummyCans = Set.empty[Pos]
  val fieldSize = 10;

  ignore("step [0 0] [0 1] robot E") {
    val step = FieldStep(FieldPos(0, 0), FieldPos(0, 1))
    val robot = RobotView(Pos(1, 1), N)
    val stages: List[Stage] = stepToStages(step, robot, fieldSize)
    val expectedStages = List(
      Stage(RobotView(Pos(1, 1), E), dummyCans),
      Stage(RobotView(Pos(1, 1), SE), dummyCans),
      Stage(RobotView(Pos(1, 1), S), dummyCans),
      Stage(RobotView(Pos(1, 2), S), dummyCans),
      Stage(RobotView(Pos(1, 3), S), dummyCans))
    assert(stages === expectedStages)
  }

  {
    case class NextDirResult(fromx: Int, fromy: Int, tox: Int, toy: Int, expectedDir: Direction)
    val validSteps = List(
      NextDirResult(0, 0, 0, 1, S),
      NextDirResult(0, 0, 1, 0, E),
      NextDirResult(1, 1, 0, 1, W),
      NextDirResult(1, 1, 1, 0, N),
      NextDirResult(1, 1, 1, 2, S),
      NextDirResult(2, 1, 1, 1, W))

    for (ndr <- validSteps) {
      test(s"next direction valid step $ndr") {
        val s = FieldStep(FieldPos(ndr.fromx, ndr.fromy), FieldPos(ndr.tox, ndr.toy))
        val nd = nextDirection(s, fieldSize)
        assert(nd === ndr.expectedDir)
      }
    }
  }
  {
    case class NextDirResult(fromx: Int, fromy: Int, tox: Int, toy: Int)
    val invalidSteps = List(
      NextDirResult(0, 0, 0, 0),
      NextDirResult(0, 0, 1, 1),
      NextDirResult(1, 1, 1, 3),
      NextDirResult(0, 1, -1, 1),
      NextDirResult(1, 1, 4, 2),
      NextDirResult(2, 2, 2, 4))

    for (ndr <- invalidSteps) {
      test(s"next direction valid step $ndr") {
        val s = FieldStep(FieldPos(ndr.fromx, ndr.fromy), FieldPos(ndr.tox, ndr.toy))
        intercept[IllegalArgumentException] {
          nextDirection(s, fieldSize)
        }
      }
    }
  }

  def nextDirection(step: FieldStep, fieldSize: Int): Direction = {
    def assertInBounds(value: Int): Unit = {
      if (value < 0 || value >= fieldSize) throw new IllegalArgumentException(s"Value $value is out of bound for field size $fieldSize. $step")
    }
    assertInBounds(step.from.x)
    assertInBounds(step.from.y)
    assertInBounds(step.to.x)
    assertInBounds(step.to.y)
    if (step.from.x == step.to.x) {
      if (step.from.y > step.to.y) {
        if (step.from.y - step.to.y > 1) throw new IllegalArgumentException(s"The step in y direction is greater than one. $step")
        else N
      } else if (step.from.y < step.to.y) {
        if (step.to.y - step.from.y > 1) throw new IllegalArgumentException(s"The step in y direction is greater than one. $step")
        else S
      } else throw new IllegalArgumentException(s"No movement. $step")
    } else if (step.from.y == step.to.y) {
      if (step.from.x > step.to.x) {
        if (step.from.x - step.to.x > 1) throw new IllegalArgumentException(s"The step in x direction is greater than one. $step")
        else W
      } else if (step.from.x < step.to.x) {
        if (step.to.x - step.from.x > 1) throw new IllegalArgumentException(s"The step in x direction is greater than one. $step")
        else E
      } else throw new IllegalArgumentException(s"No movement. $step")
    } else throw new IllegalArgumentException(s"Moved along two axes. $step")
  }

  def stepToStages(step: FieldStep, robot: RobotView, fieldSize: Int): List[Stage] = {
    val dir: Direction = nextDirection(step, fieldSize)
    ???
  }
}

case class FieldStep(from: FieldPos, to: FieldPos)

case object PathUtil {

  def pathToSteps(path: List[FieldPos]): List[FieldStep] = {
    path match {
      case Nil => throw new IllegalStateException("path must contain at least two positions")
      case a :: Nil => Nil
      case a :: b :: r => FieldStep(a, b) :: pathToSteps(b :: r)
    }
  }

  def strCodeToPath(strCode: String, seed: Long): List[FieldPos] = {
    val code: Array[Byte] = strCode.map(c => (c - 48).toByte).toArray
    val decisions = Converter.toDecisions(code)
    val ran = new Random(seed)
    val f = FieldFactory.createRandomField(ran, 10)
    FieldEvaluator.evaluate(decisions, f, ran).path
  }

}
  
