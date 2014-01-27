package clashcode.video.swing

import java.awt.Graphics2D
import scala.swing.MainFrame
import java.awt.Dimension
import scala.swing.Panel
import java.awt.Color
import clashcode.video.Device
import clashcode.video.Direction
import clashcode.video.Graphics
import clashcode.video.Pos
import clashcode.video.Stage
import clashcode.video.DrawArea
import clashcode.video.Rec
import clashcode.video.Max
import clashcode.video.EffectiveField
import clashcode.video.EffectiveOffset

trait AwtGraphics extends Graphics {

  def graphics: Graphics2D

}

trait AwtRectGraphics extends AwtGraphics {

  val da = drawArea

  def clear: Unit = {
    graphics.setColor(Color.WHITE)
    val x = da.offset.x
    val y = da.offset.y
    val w = da.area.w
    val h = da.area.h
    graphics.fillRect(x, y, w, h)
  }
  def paintField(max: Max) = {
    graphics.setColor(Color.BLACK)
    val field = EffectiveField.calc(da, 0.6, 10, 10)
    (0 to (max.x / 2) - 1).foreach(i => {
      val fw = field.area.w / (max.x / 2)
      val d = i * fw;
      graphics.drawLine(field.offset.x + d, field.offset.y, field.offset.x + d, field.offset.y + field.area.h) 
    })
    (0 to (max.y / 2) - 1).foreach(i => {
      val fh = field.area.h / (max.y / 2)
      val d = i * fh;
      graphics.drawLine(field.offset.x, field.offset.y + d, field.offset.x + field.area.w, field.offset.y + d) 
    })
    graphics.drawRect(field.offset.x, field.offset.y, field.area.w, field.area.h)
  }
  def paintCan(pos: Pos, max: Max) = {
    graphics.setColor(Color.RED)
    val o: Pos = EffectiveOffset.calc(pos, max, da, 0.6, 10, 10)
    graphics.fillRect(o.x, o.y, 5, 5)
  }
  def paintRobot(pos: Pos, dir: Direction, max: Max) = {
    graphics.setColor(Color.GREEN)
    val o: Pos = EffectiveOffset.calc(pos, max, da, 0.6, 10, 10)
    graphics.fillRect(o.x, o.y, 15, 15)
  }

}

case class SwingDevice(g: Graphics2D => AwtGraphics) extends Device {

  var _stage: Option[Stage] = None
  var _f: Option[PaintFunc] = None

  val panel = new Panel {

    override def paint(awtg: Graphics2D): Unit = {
      _stage match {
        case Some(s) => _f match {
          case Some(f) => {
            f(g(awtg), s)
          }
          case None => // Nothing to be done
        }
        case None => // Nothing to be done
      }
    }

  }

  val mf = new MainFrame()
  mf.contents = panel
  mf.size = new Dimension(500, 500)
  mf.centerOnScreen
  mf.visible = true;

  def paintStage(stage: Stage, f: PaintFunc) = {
    _stage = Some(stage)
    _f = Some(f)
    panel.repaint
  }

  def drawArea: DrawArea = {
    DrawArea(Pos(0, 0), Rec(panel.size.width, panel.size.height))
  }

}
