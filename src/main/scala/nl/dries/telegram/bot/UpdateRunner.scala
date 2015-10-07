package nl.dries.telegram.bot

import akka.actor.{Actor, ActorLogging, Props}
import akka.util.Timeout
import nl.dries.telegram.bot.UpdateRunner.{TriggerUpdate, UpdateResult}
import spray.http.{FormData, Uri}
import spray.httpx.RequestBuilding._

import scala.concurrent.duration._
import scala.util.{Failure, Success}

object UpdateRunner {

  def props(timeout: Int, apiUri: Uri) = Props(classOf[UpdateRunner], timeout, apiUri)

  sealed trait UpdateOperation

  case class TriggerUpdate(offset: Int) extends UpdateOperation

  case class UpdateResult(updates: List[Update]) extends UpdateOperation

}

class UpdateRunner(timeout: Int, apiUri: Uri) extends Actor with ActorLogging with JsonCommunicator {

  import TelegramJsonProtocol._
  import context.dispatcher

  implicit val requestTimeout = Timeout(timeout.seconds)

  override def receive: Receive = {
    case TriggerUpdate(offset) =>
      val trigger = sender()

      val params = Map("offset" -> offset.toString, "timeout" -> timeout.toString)

      sendRequest[OperationResult[List[Update]]](Post(apiUri, FormData(params))) onComplete {
        case Success(updates) =>
          trigger ! UpdateResult(updates.result)
        case Failure(ex) =>
          log.error(ex, "Error retrieving updates")
          trigger ! UpdateResult(List.empty)
      }

  }
}
