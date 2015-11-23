package nl.dries.telegram.bot

import akka.actor.{Actor, ActorLogging, Props}
import akka.util.Timeout
import spray.http.Uri
import spray.httpx.RequestBuilding._

import scala.concurrent.duration._
import scala.util.{Failure, Success}

object ChatHandler {

  case class SendMessage(messageToSend: SendableMessage)

  case class SendSucces(resultingMessage: Message, messageToSend: SendableMessage)

  case class SendFailure(messageToSend: SendableMessage, exc: Exception)

  def props(apiBase: Uri) = Props(classOf[ChatHandler], apiBase)
}

class ChatHandler(apiBase: Uri) extends Actor with JsonCommunicator with ActorLogging {

  import ChatHandler._
  import context.dispatcher

  implicit val requestTimeout = Timeout(5.seconds)

  override def receive: Receive = {
    case SendMessage(msg) =>
      val originalSender = sender()
      val uri = apiBase.withPath(apiBase.path + "/sendMessage")

      sendRequest[OperationResult[Message]](Post(uri, msg)) onComplete {
        case Success(receivedMessage) => originalSender ! SendSucces(receivedMessage.result, msg)
        case Failure(exception) => exception match {
          case ex: Exception => originalSender ! SendFailure(msg, ex)
          case thr: Throwable => log.error(thr, "Error sending request")
        }
      }
    case _ =>
  }
}
