package clashcode.video

object TextSceneTryout extends App {
  
  val framesPerSecond = 10
  
  val stageList: List[Stage] = List(
      TextStage("hallo"))
  val stages = Stages(stageList, 10)
    val device: Device = SwingDeviceFactory(framesPerSecond, stages.fieldSize).device
  device.playEndless(stages)


}