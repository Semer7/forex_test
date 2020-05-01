package forex

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.Http
import forex.actors.HolderActor



object Main extends App {

  implicit val actorSystem: ActorSystem = ActorSystem()

//  Http().bindAndHandle(RouteLogic.route, interface = "localhost", port = 8585)
  actorSystem.actorOf(Props[HolderActor])
}
