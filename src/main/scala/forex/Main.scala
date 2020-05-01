package forex

import akka.actor.{ActorSystem, Props}
import forex.actors.HolderActor



object Main extends App {

  implicit val actorSystem: ActorSystem = ActorSystem()

  actorSystem.actorOf(Props[HolderActor])
}
