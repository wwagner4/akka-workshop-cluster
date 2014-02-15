package clashcode.video.swing

import clashcode.video.Device
import clashcode.video.Stage
import java.awt.image.BufferedImage
import java.awt.Graphics2D
import java.awt.RenderingHints
import clashcode.video.CommonGraphics
import clashcode.video.Pos
import clashcode.video.DrawArea
import clashcode.video.Rec
import javax.imageio.ImageIO
import java.io.File
import clashcode.video.NumberedStage

class ImagesDevice extends Device {

  //val res = Rec(3840, 2160) // 2160p
  //val res = Rec(2560, 1440)
  val res = Rec(1920, 1080)
  //val res = Rec(1600, 900)
  //val res = Rec(640, 360)
  val imgFormat = "png" // jpg, png

  def paintStage(stage: NumberedStage): Unit = {
    println("paint stage")
    val bi = new BufferedImage(res.w, res.h, BufferedImage.TYPE_INT_RGB)
    val g2 = bi.createGraphics();
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    val cg = toCommonGraphics(g2)
    stage.stage.paint(cg)

    val home = new File(System.getProperty("user.home"))
    val outDir = new File(home, "video")
    val mkdirsOK = if (outDir.exists()) true else outDir.mkdirs()
    if (mkdirsOK) {

      val nr: String = "%05d" format stage.nr
      val fileName = s"img$nr.$imgFormat"
      val file = new File(outDir, fileName)
      val writeOK = ImageIO.write(bi, imgFormat, file)
      if (!writeOK) throw new IllegalStateException(s"Error writing image $fileName")
      println(s"Wrote file $file")
    } else {
      throw new IllegalStateException(s"Error creating directory '$outDir'")
    }
  }
  private def toCommonGraphics(g: Graphics2D): CommonGraphics = {
    val useKacheln = false
    new ImageAwtGraphics(ImageProvider_V01, useKacheln, 0.6, 0.07) {
      def graphics: Graphics2D = g
      def drawArea: DrawArea = DrawArea(Pos(0, 0), res)
    }
  }
}