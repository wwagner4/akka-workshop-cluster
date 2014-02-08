package clashcode.video

import org.scalatest.FunSuite
import clashcode.robot.Evaluator
import clashcode.robot.Converter
import clashcode.robot.FieldPos
import clashcode.video._
import scala.util.Random

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
    val path = PathUtil.strCodeToPath(strCode, new Random(200))
    assert(path.length === 200)
    assert(path.take(10) === List(FieldPos(4, 0), FieldPos(4, 1), FieldPos(4, 1), FieldPos(5, 1), FieldPos(5, 1), FieldPos(5, 2), FieldPos(5, 3), FieldPos(5, 3), FieldPos(4, 3), FieldPos(4, 4)))
  }

  test("String code to scenes") {
    val strCode = "03530311022335213110315511111120251141140200400110522540004423424544141444444444142541204404414145445445424454151340002434334143"
    val stages = SceneCreator.stringCodeToStages(strCode, 24234L)
    assert(stages.size > 100)
  }
  
  test("Split path to steps") {
    val strCode = "03530311022335213110315511111120251141140200400110522540004423424544141444444444142541204404414145445445424454151340002434334143"
    val path = PathUtil.strCodeToPath(strCode, new Random(200)).take(4)
    val steps: List[FieldStep] = PathUtil.pathToSteps(path)
    val expectedSteps = List(
      FieldStep(FieldPos(4, 0), FieldPos(4, 1)),
      FieldStep(FieldPos(4, 1), FieldPos(4, 1)),
      FieldStep(FieldPos(4, 1), FieldPos(5, 1)))
    assert(steps === expectedSteps)
  }

  val dummyCans = Set.empty[Pos]
  val fieldSize = 10;

  test("step [0 0] [0 1] robot N") {
    val step = FieldStep(FieldPos(0, 0), FieldPos(0, 1))
    val robot = RobotView(Pos(1, 1), N)
    val stages: List[Stage] = PathUtil.stepToStages(step, robot, fieldSize, new Random(2))
    val expectedStages = List(
      Stage(RobotView(Pos(1, 1), NE), dummyCans),
      Stage(RobotView(Pos(1, 1), E), dummyCans),
      Stage(RobotView(Pos(1, 1), SE), dummyCans),
      Stage(RobotView(Pos(1, 1), S), dummyCans),
      Stage(RobotView(Pos(1, 2), S), dummyCans),
      Stage(RobotView(Pos(1, 3), S), dummyCans))
    assert(stages === expectedStages)
  }

  test("step [0 0] [1 0] robot N") {
    val step = FieldStep(FieldPos(0, 0), FieldPos(1, 0))
    val robot = RobotView(Pos(1, 1), N)
    val stages: List[Stage] = PathUtil.stepToStages(step, robot, fieldSize, new Random(123))
    val expectedStages = List(
      Stage(RobotView(Pos(1, 1), NE), dummyCans),
      Stage(RobotView(Pos(1, 1), E), dummyCans),
      Stage(RobotView(Pos(2, 1), E), dummyCans),
      Stage(RobotView(Pos(3, 1), E), dummyCans))
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
        val nd = PathUtil.nextDirection(s, fieldSize)
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
          PathUtil.nextDirection(s, fieldSize)
        }
      }
    }
  }
  {
    case class DiffResult(from: Direction, to: Direction, expected: Int)
    val situations = List(
      DiffResult(N, NE, 1),
      DiffResult(N, E, 2),
      DiffResult(N, S, 4),
      DiffResult(N, SW, -3),
      DiffResult(E, E, 0),
      DiffResult(E, W, 4),
      DiffResult(E, S, 2),
      DiffResult(E, N, 2))
    for (s <- situations) {
      test(s"Direction diff $s") {
        val d = DirectionUtil.diff(s.from, s.to)
        assert(d === s.expected)
      }
    }
  }
  {
    case class TurnListResult(startDir: Direction, times: Int, expected: List[Direction])
    val situations = List(
      TurnListResult(N, 1, List(NE)),
      TurnListResult(N, 2, List(NE, E)),
      TurnListResult(N, -1, List(NW)))
    for (s <- situations) {
      test(s"Turn list $s") {
        val l = DirectionUtil.turnList(s.startDir, s.times)
        assert(l === s.expected)
      }
    }
  }
  

}


