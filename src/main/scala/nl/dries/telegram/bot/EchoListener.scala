package nl.dries.telegram.bot

import akka.actor.{Actor, ActorRef, Props}

object EchoListener {
  def props(poller: ActorRef): Props = Props(classOf[EchoListener], poller)
}

/**
 * Simple 'echo' listener responder to updates
 */
class EchoListener(poller: ActorRef) extends Actor {

  import UpdatePoller._

  /** Register listener */
  poller ! AddListener(self)

  override def receive: Receive = {
    case NewUpdate(update) =>
      println("Update receieved")

  }

  override def postStop() = poller ! RemoveListener(self)
}
