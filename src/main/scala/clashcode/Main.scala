package clashcode

import akka.actor.{Props, ActorSystem}
import akka.routing.{BroadcastRouter, RandomRouter, ConsistentHashingRouter, FromConfig}
import akka.cluster.routing.{ClusterRouterSettings, ClusterRouterConfig}
import com.typesafe.config.{ConfigValue, ConfigFactory}

object Main extends App {

  override def main(args: Array[String]) {

    val maybePortConfig = args.headOption.map(port => ConfigFactory.parseString("akka.remote.netty.tcp.port = " + port))
    val config = maybePortConfig.map(_.withFallback(ConfigFactory.load)).getOrElse(ConfigFactory.load)
    val username = config.getString("akka.username")

    val system = ActorSystem("cluster", config)

    val router = system.actorOf(Props.empty.withRouter(
      ClusterRouterConfig(
        BroadcastRouter(),
        ClusterRouterSettings(totalInstances = 100, routeesPath = "/user/main", allowLocalRoutees = true, useRole = None))),
      name = "router")

    system.actorOf(Props(classOf[MainActor], router, username), "main")

    readLine()
    system.shutdown()
  }

}