package clashcode.video

import java.awt.Graphics2D
import clashcode.video.swing._
import scala.concurrent.duration._
import clashcode.video.lists._

trait VideoList {
  def videos: List[Video]
}

object VideoMain extends App {

  val framesPerSecond = 14
  //val vl: VideoList = AkkaWorkshopPresentationVideos
  val vl: VideoList = AkkaWorkshopResultsVideos

  val stages = vl.videos.flatMap(v => VideoCreator.create(v, framesPerSecond))
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