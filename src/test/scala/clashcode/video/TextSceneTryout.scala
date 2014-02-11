package clashcode.video

object TextSceneTryout extends App {
  

  
  val framesPerSecond = 1
  
  val longText = """
For the Java Editor breadcrumb, 
you need to assign a shortcut to the 
"Toggle Java Editor Breadcrumb" command 
(I have tested Alt+B, for instance)
""".split("\n").toList
  
  val stageList: List[Stage] = List(
      TextStage(Text(longText)))
    val device: Device = SwingDeviceFactory(framesPerSecond).device
  device.playEndless(stageList)


}