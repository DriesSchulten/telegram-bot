package nl.dries.telegram.bot

import akka.actor.{Actor, ActorLogging, Props}
import akka.util.Timeout
import spray.client.pipelining._
import spray.http._
import spray.http.Uri.Path

import scala.concurrent.duration._
import scala.util.{Failure, Success}

object ChatHandler {

  case class SendMessage(messageToSend: SendableMessage)

  case class SendSucces(resultingMessage: Message, messageToSend: SendableMessage)

  case class SendFailure(messageToSend: SendableMessage, exc: Exception)

  def props(apiBase: Uri) = Props(classOf[ChatHandler], apiBase)
}

class ChatHandler(apiBase: Uri) extends Actor with ActorLogging with JsonCommunicator {

  import context.dispatcher
  import ChatHandler._
  import TelegramJsonProtocol._

  implicit val requestTimeout = Timeout(5.seconds)

  override def receive: Receive = {
    case SendMessage(msg) =>
      val originalSender = sender()
      val uri = apiBase.withPath(Path("/sendMessage"))

      sendRequest[Message](Post(uri, msg)) onComplete {
        case Success(receivedMessage) => originalSender ! SendSucces(receivedMessage, msg)
        case Failure(exception) => exception match {
          case ex: Exception => originalSender ! SendFailure(msg, ex)
          case thr: Throwable => log.error(thr, "Error sending request")
        }
      }
    case _ =>
  }
}
