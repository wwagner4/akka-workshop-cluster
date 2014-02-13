package clashcode.video

import scala.io.Codec

case class ResultEntry(fitness: Int, name: String, code: String)

object VideoEngine extends App {

  println("Reading File")
  val results: List[ResultEntry] = readFile("workshop-erge.txt").sortBy(_.fitness).reverse
  println(results.mkString("\n"))

  private def readFile(filename: String): List[ResultEntry] = {
    import scala.io.Source
    import java.io.File
    val linesList = Source.fromFile(new File(filename))(Codec.UTF8).getLines().toSet
    val set = linesList.map(parseLine(_))
    set.toList
  }

  private def parseLine(line: String): ResultEntry = {
    val lineItems = line.split("\\s+").filter(_ != "")
    assert(lineItems.size == 3, s"Size of ${lineItems.toList.mkString("##")} must be 3")
    ResultEntry(lineItems(0).trim.toInt, lineItems(1).trim, lineItems(2).trim)
  }
}