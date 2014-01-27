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


trait AwtGraphics extends Graphics {
  
  def graphics: Graphics2D
  
}

trait AwtRectGraphics extends AwtGraphics {
  
  val da = drawArea
  
  val x = da.offset.x
  val y = da.offset.y
  val w = da.area.w
  val h = da.area.h

  def clear: Unit = {
    graphics.setColor(Color.WHITE)
    graphics.fillRect(x, y, w, h)
  }
  def paintField = {
    graphics.setColor(Color.BLACK)
    graphics.drawRect(x + 5, y + 5, w - 10, h - 10)
  }
  def paintCan(pos: Pos) = {
    graphics.setColor(Color.RED)
    graphics.fillRect(x + pos.x * 10, y + pos.y * 10, 5, 5)
  }
  def paintRobot(pos: Pos, dir: Direction) = {
    graphics.setColor(Color.GREEN)
    graphics.fillRect(x + pos.x * 10, y + pos.y * 10, 5, 10)
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
