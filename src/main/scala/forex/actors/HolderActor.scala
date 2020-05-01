package forex.actors

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.routing.RoundRobinPool
import forex.RouteLogic

import scala.concurrent.ExecutionContextExecutor
import scala.util.Try

class HolderActor extends Actor with ActorLogging {

  implicit val executionContext: ExecutionContextExecutor = context.dispatcher
  implicit val system: ActorSystem = context.system
  private val ratesActor: ActorRef = context.actorOf(Props[RatesActor])
  private val httpRouterRef: ActorRef = context.actorOf(
    RoundRobinPool(getIntForKey("httpProcessorsParallelism")).props(Props(classOf[HttpProcessActor], ratesActor))
  )

  override def preStart(): Unit = {
    super.preStart()

    Http().bindAndHandle(RouteLogic.route(httpRouterRef, log), "127.0.0.1", 8585)
  }

  override def receive: Receive = Actor.emptyBehavior

  private def getIntForKey(key:String, default: Int = 1): Int = {
    Try { system.settings.config.getInt(key)}.getOrElse(default)
  }
}
