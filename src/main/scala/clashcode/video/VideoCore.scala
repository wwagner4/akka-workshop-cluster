package clashcode.video

case class Pos(x: Int, y: Int)
case class Max(x: Int, y: Int)
case class Rec(w: Int, h: Int)

case class DrawArea(offset: Pos, area: Rec)

sealed trait Direction

case object N extends Direction
case object NE extends Direction
case object E extends Direction
case object SE extends Direction
case object S extends Direction
case object SW extends Direction
case object W extends Direction
case object NW extends Direction

case class RobotView(pos: Pos, dir: Direction)

case class NumberedStage(nr: Int, stage: Stage)

sealed trait Stage {
  def paint(g: CommonGraphics): Unit
}

case class GameStage(robot: RobotView, cans: Set[Pos], fieldSize: Int) extends Stage {

  val max = Max(fieldSize * 2, fieldSize * 2)

  def paint(g: CommonGraphics): Unit = {
    g.clear
    val visibleCans = cans - robot.pos
    g.paintField(max)
    for (c <- visibleCans) {
      g.paintCan(c, max)
    }
    g.paintRobot(robot.pos, robot.dir, max)
  }

}

case class TextStage(text: Text) extends Stage {
  def paint(g: CommonGraphics): Unit = {
    g.clear
    g.paintText(text)
  }

}

trait Device {

  // Define how to paint a stage on that device
  def paintStage(stage: NumberedStage)

  def postPaintStage: Unit = {
    // Do nothing by default
  }

  def playEndless(stages: List[NumberedStage]): Unit = {
    assert(stages.nonEmpty, "Stages must not be empty")
    while (true) {
      stages.foreach(s => {
        paintStage(s)
        postPaintStage
      })
    }
  }

  def playOnes(stages: List[NumberedStage]): Unit = {
    stages.foreach(s => {
      paintStage(s)
      postPaintStage
    })
  }

}

/**
 * Abstraction level for Graphics
 * Can, but must not be used from Device implementations
 */

case class Text(lines: List[String])

sealed trait CommonColor
case object Black extends CommonColor
case object White extends CommonColor

trait CommonGraphics {
  
  def imgProvider: ImageProvider
  
  def widthHeightRatio: Double = 0.8
  def border: Double = 0.05

  def drawImage(vimg: VideoImage, pos: Pos, scale: Double)

  def clear: Unit = {
    setColor(White)
    val x = drawArea.offset.x
    val y = drawArea.offset.y
    val w = drawArea.area.w
    val h = drawArea.area.h
    fillRect(x, y, w, h)
  }

  def setColor(c: CommonColor)
  def drawArea: DrawArea
  
  def drawLine(fromx: Int, fromy: Int, tox: Int, toy: Int)
  def drawRect(p1x: Int, p1y: Int, p2x: Int, p2y: Int)
  def fillRect(p1x: Int, p1y: Int, p2x: Int, p2y: Int)
  def paintField(max: Max): Unit = {
    setColor(Black)
    val field = EffectiveField.calc(drawArea, widthHeightRatio, border)
    (0 to (max.x / 2) - 1).foreach(i => {
      val fw = field.area.w / (max.x / 2)
      val d = i * fw;
      drawLine(field.offset.x + d, field.offset.y, field.offset.x + d, field.offset.y + field.area.h)
    })
    (0 to (max.y / 2) - 1).foreach(i => {
      val fh = field.area.h / (max.y / 2)
      val d = i * fh;
      drawLine(field.offset.x, field.offset.y + d, field.offset.x + field.area.w, field.offset.y + d)
    })
    drawRect(field.offset.x, field.offset.y, field.area.w, field.area.h)
  }
  def paintCan(pos: Pos, max: Max) = {
    val vimg = imgProvider.can
    val img = vimg.image
    val f = EffectiveField.calc(drawArea, widthHeightRatio, border)
    val o: Pos = EffectiveOffset.calc(pos, max, f)
    val fw = f.area.w
    val s = fw.toDouble / vimg.shrinkFactor

    drawImage(vimg, o, s)
  }

  def paintRobot(pos: Pos, dir: Direction, max: Max) = {
    val videoImage = imgProvider.robots(dir)
    val f = EffectiveField.calc(drawArea, widthHeightRatio, border)
    val o: Pos = EffectiveOffset.calc(pos, max, f)
    val fw = f.area.w
    val s = fw.toDouble / videoImage.shrinkFactor
    drawImage(videoImage, o, s)
  }

  def setFontSize(size: Double)
  
  def drawString(str: String, x: Int, y: Int)
  
  def paintText(text: Text) = {
    setColor(Black)
    val fontSize = drawArea.area.h.toFloat / 25
    setFontSize(fontSize)
    val lines = text.lines
    for (i <- 0 until lines.size) {
      if (i == 1) {
	    val fontSize = drawArea.area.h.toFloat / 30
	    setFontSize(fontSize)
      }
      val y = (10 + fontSize * (i + 1)).toInt
      drawString(lines(i), 30, y)
    }
  }
}

case class VideoImage(image: java.net.URL, centerx: Double, centery: Double, shrinkFactor: Int)

trait ImageProvider {

  def robots: Map[Direction, VideoImage]
  def can: VideoImage

  def img(resName: String): java.net.URL = {
    this.getClass().getClassLoader().getResource(resName)
  }

}

object ImageProvider_V02 extends ImageProvider {

  lazy val robots: Map[Direction, VideoImage] = {
    val imgNames = List(
      (S, "img/robots/r00.png"),
      (SE, "img/robots/r01.png"),
      (E, "img/robots/r02.png"),
      (NE, "img/robots/r03.png"),
      (N, "img/robots/r04.png"),
      (NW, "img/robots/r05.png"),
      (W, "img/robots/r06.png"),
      (SW, "img/robots/r07.png"))
    imgNames.map { case (key, name) => (key, VideoImage(img(name), 0.5, 0.6, 2000)) }.toMap
  }

  lazy val can: VideoImage = {
    VideoImage(img("img/cans/can.png"), 0.5, 0.5, 2000)
  }
}

object ImageProvider_V01 extends ImageProvider {


  lazy val robots: Map[Direction, VideoImage] = {
    val imgNames = List(
      (S, "img01/robots/r00.png"),
      (SE, "img01/robots/r01.png"),
      (E, "img01/robots/r02.png"),
      (NE, "img01/robots/r03.png"),
      (N, "img01/robots/r04.png"),
      (NW, "img01/robots/r05.png"),
      (W, "img01/robots/r06.png"),
      (SW, "img01/robots/r07.png"))
    imgNames.map { case (key, name) => (key, VideoImage(img(name), 0.47, 0.7, 2300)) }.toMap
  }

  lazy val can: VideoImage = {
    VideoImage(img("img01/cans/can.png"), 0.5, 0.65, 1900)
  }
}



