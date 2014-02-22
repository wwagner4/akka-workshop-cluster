package clashcode.video

import java.awt.Graphics2D
import clashcode.video.swing._
import scala.concurrent.duration._
import clashcode.video.lists._

object VideoMain extends App {

  val framesPerSecond = 20

  //val vl = AkkaWorkshopPresentationVideos.videos
  //val vl = AkkaWorkshopWinnerVideos.winner
  //val vl = AkkaWorkshopWinnerVideos.next
  //val vl = AkkaWorkshopWinnerVideos.noPhilip
  //val vl = AkkaWorkshopWinnerVideos.stuck

  val vl = List(AkkaWorkshopResultsVideos.v001)

  val params = StageParams(10, ImageProvider_V01, 0.7, 0.1)
  val stages = VideoCreator.create(vl, framesPerSecond)

  val device: Device = SwingDeviceFactory(framesPerSecond).device
  //val device: Device = new ImagesDevice

  device.playOnes(stages)
  //device.playEndless(stages)

  case class SwingDeviceFactory(framesPerSecond: Int) {

    def device: Device = _device

    private lazy val _device: SwingDevice = new SwingDevice(framesPerSecond, createGraphics, params)

    private def createGraphics(g: Graphics2D): CommonGraphics = {
      new ImageAwtGraphics(g) {
        def drawArea: DrawArea = _device.determineCalcArea
      }
    }
  }
}

