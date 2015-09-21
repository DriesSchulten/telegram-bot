package nl.dries.telegram.bot

import akka.actor.{Actor, ActorLogging, Props}
import akka.util.Timeout
import nl.dries.telegram.bot.UpdateRunner.{TriggerUpdate, UpdateResult}
import spray.client.pipelining._
import spray.http.{FormData, HttpRequest, HttpResponse}

import scala.concurrent.duration._
import scala.util.{Failure, Success}

object UpdateRunner {

  sealed trait UpdateOperation

  case class TriggerUpdate(offset: Int) extends UpdateOperation

  case class UpdateResult(updates: List[Update]) extends UpdateOperation

  def props(timeout: Int, token: String) = Props(classOf[UpdateRunner], timeout, token)
}

class UpdateRunner(timeout: Int, token: String) extends Actor with ActorLogging {

  import TelegramJsonProtocol._
  import context.dispatcher
  import spray.httpx.SprayJsonSupport._

  implicit val requestTimeout = Timeout(timeout seconds)

  /** Loggers */
  val logRequest: HttpRequest => HttpRequest = { r => log.info(r.toString); r }
  val logResponse: HttpResponse => HttpResponse = { r => log.info(r.toString); r }

  private val pipeline = logRequest ~> sendReceive ~> logResponse ~> unmarshal[Updates]

  override def receive: Receive = {
    case TriggerUpdate(offset) =>
      val trigger = sender()

      val params = Map("offset" -> offset.toString, "timeout" -> timeout.toString)

      pipeline(Post(s"https://api.telegram.org/bot$token/getupdates", FormData(params))) onComplete {
        case Success(updates) =>
          trigger ! UpdateResult(updates.result)
        case Failure(ex) =>
          log.error(ex, "Error retrieving updates")
          trigger ! UpdateResult(List.empty)
      }

  }
}
