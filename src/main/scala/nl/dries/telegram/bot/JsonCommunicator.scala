package nl.dries.telegram.bot

import akka.actor.Actor
import akka.event.Logging
import akka.util.Timeout
import spray.client.pipelining._
import spray.http.HttpRequest
import spray.httpx.SprayJsonSupport
import spray.httpx.unmarshalling.FromMessageUnmarshaller

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.{Failure, Success, Try}

/**
 * JSON communicator
 */
trait JsonCommunicator extends SprayJsonSupport {
  self: Actor =>

  import context.dispatcher

  val logging = Logging(context.system, getClass)

  def sendRequest[T: FromMessageUnmarshaller : Manifest](request: HttpRequest): Future[T] = {
    val pipeline = logRequest(logging) ~> sendReceive ~> logResponse(logging) ~> unmarshal[T]
    pipeline(request)
  }
}
