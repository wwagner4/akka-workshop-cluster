package clashcode.video

case class Pos(x: Int, y: Int)
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

case class StageParams(
    fieldSize: Int, 
    imgProvider: ImageProvider, 
    widthHeightRatio: Double, border: Double)

sealed trait Stage {
  def paint(g: CommonGraphics): Unit
}

case class GameStage(robot: RobotView, cans: Set[Pos], params: StageParams) extends Stage {

  def paint(g: CommonGraphics): Unit = {
    val p = StagesPainter(g, params.imgProvider, params.widthHeightRatio, params.border)
    p.clear
    val visibleCans = cans - robot.pos
    p.paintField(params.fieldSize)
    for (c <- visibleCans) {
      p.paintCan(c, params.fieldSize)
    }
    p.paintRobot(robot.pos, robot.dir, params.fieldSize)
  }
}

case class TextStage(text: Text, params: StageParams) extends Stage {
  def paint(g: CommonGraphics): Unit = {
    val p = StagesPainter(g, params.imgProvider, params.widthHeightRatio, params.border)
    p.clear
    p.paintText(text)
  }
}
  

case class StagesPainter(g: CommonGraphics, imgProvider: ImageProvider, widthHeightRatio: Double, border: Double) {
  

  def clear: Unit = {
    g.setColor(White)
    val x = g.drawArea.offset.x
    val y = g.drawArea.offset.y
    val w = g.drawArea.area.w
    val h = g.drawArea.area.h
    g.fillRect(x, y, w, h)
  }

  def paintField(fieldSize: Int): Unit = {
    g.setColor(Black)
    val field = EffectiveField.calc(g.drawArea, widthHeightRatio, border)
    (0 to (fieldSize) - 1).foreach(i => {
      val fw = field.area.w / (fieldSize)
      val d = i * fw
      g.drawLine(field.offset.x + d, field.offset.y, field.offset.x + d, field.offset.y + field.area.h)
    })
    (0 to (fieldSize) - 1).foreach(i => {
      val fh = field.area.h / (fieldSize)
      val d = i * fh
      g.drawLine(field.offset.x, field.offset.y + d, field.offset.x + field.area.w, field.offset.y + d)
    })
    g.drawRect(field.offset.x, field.offset.y, field.area.w, field.area.h)
  }
  def paintCan(pos: Pos, fieldSize: Int) = {
    val vimg = imgProvider.can
    val img = vimg.image
    val f = EffectiveField.calc(g.drawArea, widthHeightRatio, border)
    val epos: Pos = EffectiveOffset.calc(pos, fieldSize, f)
    val fw = f.area.w
    val s = fw.toDouble / vimg.shrinkFactor
    g.drawImage(vimg, epos, s)
  }

  def paintRobot(pos: Pos, dir: Direction, fieldSize: Int) = {
    val videoImage = imgProvider.robots(dir)
    val f = EffectiveField.calc(g.drawArea, widthHeightRatio, border)
    val o: Pos = EffectiveOffset.calc(pos, fieldSize, f)
    val fw = f.area.w
    val s = fw.toDouble / videoImage.shrinkFactor
    g.drawImage(videoImage, o, s)
  }
  def paintText(text: Text) = {
    g.setColor(Black)
    val fontSize = g.drawArea.area.h.toFloat / 25
    g.setFontSize(fontSize)
    val lines = text.lines
    for (i <- 0 until lines.size) {
      if (i == 1) {
	    val fontSize = g.drawArea.area.h.toFloat / 30
	    g.setFontSize(fontSize)
      }
      val y = (10 + fontSize * (i + 1)).toInt
      g.drawString(lines(i), 30, y)
    }
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

case class VideoImage(image: java.net.URL, centerx: Double, centery: Double, shrinkFactor: Int)

trait ImageProvider {

  def robots: Map[Direction, VideoImage]
  def can: VideoImage

  def img(resName: String): java.net.URL = {
    this.getClass().getClassLoader().getResource(resName)
  }

}

trait CommonGraphics {

  def drawArea: DrawArea

  def drawImage(vimg: VideoImage, pos: Pos, scale: Double)
  def setColor(c: CommonColor)
  def drawLine(fromx: Int, fromy: Int, tox: Int, toy: Int)
  def drawRect(p1x: Int, p1y: Int, p2x: Int, p2y: Int)
  def fillRect(p1x: Int, p1y: Int, p2x: Int, p2y: Int)
  def setFontSize(size: Double)
  def drawString(str: String, x: Int, y: Int)

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



