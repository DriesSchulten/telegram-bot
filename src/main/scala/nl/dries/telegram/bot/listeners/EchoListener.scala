package nl.dries.telegram.bot.listeners

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import nl.dries.telegram.bot.ChatHandler.{SendFailure, SendMessage, SendSucces}
import nl.dries.telegram.bot._
import spray.http.Uri

object EchoListener {
  def props(poller: ActorRef): Props = Props(classOf[EchoListener], poller)
}

/**
 * Simple 'echo' listener responder to updates
 */
class EchoListener(poller: ActorRef) extends Actor with ActorLogging {

  import UpdatePoller._

  val appConfig = AppConfig(context.system)

  val chatActor = context.actorOf(ChatHandler.props(Uri(appConfig.botApi)))

  var awaitingReplies = Set.empty[SendableMessage]

  /** Register listener */
  poller ! AddListener(self)

  override def receive: Receive = {
    case NewUpdate(update) =>
      update.message.text match {
        case Some(text) =>
          log.info("Got message, sending reply")
          val sender = update.message.chat.fold(u => u, g => g)
          val reply = new SendableText(sender, text)
          chatActor ! SendMessage(reply)
          awaitingReplies = awaitingReplies + reply
        case None => log.info("No text in message with id")
      }

    case SendSucces(resultMessage, sendableMessage) =>
      awaitingReplies = awaitingReplies - sendableMessage
      log.info("Succesfully send reply")

    case SendFailure(sendableMessage, exception) =>
      awaitingReplies = awaitingReplies - sendableMessage
      log.error(exception, "Got an error sending reply")
  }

  override def postStop() = poller ! RemoveListener(self)
}
