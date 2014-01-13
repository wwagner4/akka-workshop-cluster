package clashcode

import akka.actor.{Props, ActorSystem}
import akka.routing.{BroadcastRouter, RandomRouter, ConsistentHashingRouter, FromConfig}
import akka.cluster.routing.{ClusterRouterSettings, ClusterRouterConfig}
import com.typesafe.config.{ConfigValue, ConfigFactory}

object Main extends App {

  override def main(args: Array[String]) {

    val port = args(0).toInt
    val mainConfig = ConfigFactory.parseString("akka.remote.netty.tcp.port = " + port)
    val config = mainConfig.withFallback(ConfigFactory.load)
    //println(config.entrySet().toString)
    //throw new Exception("")

    val system = ActorSystem("cluster", config)

    val router = system.actorOf(Props.empty.withRouter(
      ClusterRouterConfig(
        BroadcastRouter(),
        ClusterRouterSettings(totalInstances = 100, routeesPath = "/user/main", allowLocalRoutees = true, useRole = None))),
      name = "router")

    system.actorOf(Props(classOf[MainActor], router, port), "main")

    readLine()
    system.shutdown()
    //system2.shutdown()
  }

}