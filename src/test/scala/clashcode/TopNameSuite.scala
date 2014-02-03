package clashcode

import org.scalatest.FunSuite

class TopNameSuite extends FunSuite {

  test("two relevant names") {
    val map = Map("A" -> 7, "B" -> 8)
    assert(TopName.name(map) === "[B][A]")
  }

  test("two relevant names reverse order") {
    val map = Map("A" -> 8, "B" -> 7)
    assert(TopName.name(map) === "[A][B]")
  }

  test("three and two relevant") {
    val map = Map("A" -> 8, "B" -> 7, "C" -> 3)
    assert(TopName.name(map) === "[A][B]")
  }

  test("three and two relevant other order") {
    val map = Map("A" -> 8, "C" -> 3, "B" -> 7)
    assert(TopName.name(map) === "[A][B]")
  }

  test("one relevant") {
    val map = Map("A" -> 8, "C" -> 3, "B" -> 1)
    assert(TopName.name(map) === "[A]")
  }

}