package clashcode.video.swing

import java.awt.Graphics2D
import scala.swing.MainFrame
import java.awt.Dimension
import scala.swing.Panel
import java.awt.Color
import clashcode.video._
import javax.imageio.ImageIO
import sun.java2d.pipe.BufferedBufImgOps
import java.awt.geom.AffineTransform
import java.awt.image.AffineTransformOp
import java.awt.image.BufferedImage
import scala.util.Random
import javax.swing.ImageIcon

case class SwingDevice(framesPerSecond: Int, f: Graphics2D => CommonGraphics)
  extends Device {

  var _stage: Option[NumberedStage] = None

  def paintStage(stage: NumberedStage) = {
    _stage = Some(stage)
    panel.repaint
  }

  override def postPaintStage: Unit = {
    Thread.sleep((1000.0 / framesPerSecond).toInt);
  }

  val panel = new Panel {

    override def paint(awtg: Graphics2D): Unit = {
      _stage match {
        case Some(s) => s.stage.paint(f(awtg))
        case None => // Nothing to be done
      }
    }

  }

  val mf = new MainFrame()
  mf.contents = panel
  mf.title = "Akka Workshop Reloaded"
  mf.iconImage = new ImageIcon(getClass.getClassLoader().getResource("icon.png")).getImage
  mf.size = mf.toolkit.getScreenSize()
  mf.visible = true;

  def determineCalcArea: DrawArea = {
    DrawArea(Pos(0, 0), Rec(panel.size.width, panel.size.height))
  }

}

trait AwtGraphics extends CommonGraphics {

  def graphics: Graphics2D

}

abstract class RectangularAwtGraphics(widthHeightRatio: Double, border: Double) extends AwtGraphics {

}

abstract class ImageAwtGraphics(val imgProvider: ImageProvider, useKacheln: Boolean, widthHeightRatio: Double, border: Double)
  extends RectangularAwtGraphics(widthHeightRatio, border) {

  def drawImage(vimg: VideoImage, pos: Pos, scale: Double): Unit = {
	val icon = new ImageIcon(vimg.image)
	val imgoffx = (icon.getIconWidth().toDouble * scale * vimg.centerx).toInt
    val imgoffy = (icon.getIconHeight().toDouble * scale * vimg.centery).toInt
    val transform = AffineTransform.getTranslateInstance(pos.x - imgoffx, pos.y - imgoffy)
    transform.concatenate(AffineTransform.getScaleInstance(scale, scale))
    graphics.drawImage(icon.getImage(), transform, null)
  }
  def drawLine(fromx: Int, fromy: Int, tox: Int, toy: Int): Unit = {
    graphics.drawLine(fromx, fromy, tox, toy)
  }
  def drawRect(x: Int, y: Int, w: Int, h: Int): Unit = {
    graphics.drawRect(x, y, w, h)
  }
  def fillRect(x: Int, y: Int, w: Int, h: Int): Unit = {
    graphics.fillRect(x, y, w, h)
  }
  def drawString(str: String, x: Int, y: Int): Unit = {
    graphics.drawString(str, x, y)
  }
  def setColor(c: CommonColor): Unit = {
    c match {
      case Black => graphics.setColor(Color.BLACK)
      case White => graphics.setColor(Color.WHITE)
    }
  }
  def setFontSize(size: Double): Unit = {
    val font = graphics.getFont()
    val fontSize = drawArea.area.h.toFloat / 25
    graphics.setFont(font.deriveFont(fontSize))
  }

}

