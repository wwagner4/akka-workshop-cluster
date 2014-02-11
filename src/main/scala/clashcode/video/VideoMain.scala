package clashcode.video

import java.awt.Graphics2D
import clashcode.video.swing._
import scala.concurrent.duration._

object VideoMain extends App {

  val video = Video("A really bad robot\nShowing only the first steps, the rest is boring\n\nfitness = -10",
    3.second,
    "03530311022335213110315511111120251141140200400110522540004423424544141444444444142541204404414145445445424454151340002434334143",
    Some(50),
    238476L)

  val framesPerSecond = 15

  val stages = VideoCreator.create(video: Video, framesPerSecond)
  val device: Device = SwingDeviceFactory(framesPerSecond).device
  device.playOnes(stages)

}

case class SwingDeviceFactory(framesPerSecond: Int) {
  
  def device: Device = _device
  
  private lazy val _device: SwingDevice = new SwingDevice(framesPerSecond, createGraphics)

  private def createGraphics(g: Graphics2D): AwtGraphics = {
    val useKacheln = false
    new ImageAwtGraphics(ImageProvider_V01, useKacheln, 0.6, 0.07) {
      def graphics: Graphics2D = g
      def drawArea: DrawArea = _device.determineCalcArea
    }
  }
}