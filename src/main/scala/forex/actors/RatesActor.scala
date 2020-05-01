package forex.actors

import java.nio.charset.StandardCharsets
import java.util.concurrent.TimeUnit

import akka.actor.{Actor, ActorLogging, ActorSystem}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpHeader.ParsingResult
import akka.http.scaladsl.model.Uri.Query
import akka.http.scaladsl.model.{HttpHeader, HttpRequest, Uri}
import akka.util.ByteString
import forex.model.{Currency, Rates, RatesUpdatePair}
import play.api.libs.json.{JsError, JsSuccess, Json}

import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration.{Duration, FiniteDuration}
import scala.util.{Failure, Success}

class RatesActor extends Actor with ActorLogging {

  private var lastUpdated: Long = Long.MinValue
  private var rates: Option[Rates] = None
  implicit val system:ActorSystem = context.system
  implicit val executionContext: ExecutionContextExecutor = context.dispatcher


  override def preStart(): Unit = {
    log.info("Rates actor started")
    context.system.scheduler
      .scheduleAtFixedRate(Duration.Zero, FiniteDuration(3, TimeUnit.MINUTES), self, RequestUpdates())

  }

  override def receive: Receive = {
    case RequestUpdates() =>
      log.info("Received request updates")
      val query = Currency.getAllCurrencyPairs.map(pair => ("pair", pair._1 + pair._2))
        .foldLeft(Query.newBuilder)((builder, tuple) => builder.addOne(tuple)).result()
      val requestUri = Uri.from(scheme = "http", host = "localhost", port = 8080, path = "/rates")
        .withQuery(query)
      val httpRequest = HttpRequest(uri = requestUri,
        headers = Seq(getAuthHeader.get))

      Http().singleRequest(httpRequest).onComplete({
        case Failure(exception) =>
          log.error(exception, "Querying rates update from one-frame")
        case Success(value) =>
          if (value.status.isSuccess()) {
            value.entity.dataBytes.runFold(ByteString.empty)(_ ++ _)
              .map(result => self ! UpdateResult(result) )
          } else if (value.status.isFailure()) {
            log.error(s"Failure response ${value.status}")
          }


      })

    case UpdateResult(byteString) =>
      log.info("Received update result")
      parseUpdate(byteString) match {
        case JsSuccess(value, _) =>
          rates = Some(Rates(value))
          lastUpdated = System.currentTimeMillis()
        case JsError(errors) =>
          log.error(errors.toString())
      }

    case message@GetRates() =>
      log.info("Received get rates")
      if (lastUpdated < 0 || System.currentTimeMillis() - lastUpdated > 5 * 60 * 1000) {
        log.info("Data is too old, resending message to self")
        context.system.scheduler
          .scheduleOnce(FiniteDuration(5, TimeUnit.SECONDS), self, message)(executionContext, sender())
      } else {

        sender() ! RatesUpdate(rates.get, lastUpdated)
      }
    case x =>
      log.error(s"Unknown message received $x")

  }

  override def postStop(): Unit = {
    super.aroundPostStop()
    log.info("Rates actor stopped")
  }

  private def getAuthHeader: Option[HttpHeader] = {
    val tokenValue = system.settings.config.getString("security.one-frame-token")

    HttpHeader.parse("token", tokenValue) match {
      case ParsingResult.Ok(header, errors) => Some(header)
      case ParsingResult.Error(error) => None
    }
  }

  private def parseUpdate(byteString: ByteString) = {
    val string = byteString.decodeString(StandardCharsets.UTF_8)
    log.info(s"Byte string: $string")
    Json.parse(string).validate[List[RatesUpdatePair]]
  }


}

case class RequestUpdates()
case class UpdateResult(byteString: ByteString)
case class GetRates()
