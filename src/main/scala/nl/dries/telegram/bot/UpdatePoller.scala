package nl.dries.telegram.bot

import akka.actor.{Actor, ActorRef, Cancellable, Props}

import scala.concurrent.duration._

object UpdatePoller {

  case class NewUpdate(update: Update)

  def props() = Props(classOf[UpdatePoller])
}

class UpdatePoller extends Actor {

  import UpdatePoller._
  import UpdateRunner._
  import context.dispatcher

  /** Request timeout to use */
  val timeout: Int = 20

  /** Last received counter */
  var lastReceivedUpdate: Option[Int] = None

  /** Set of listeners to updates */
  var updateListeners = Set.empty[ActorRef]

  /** Runner actor */
  val botConfig = context.system.settings.config.getConfig("bot")
  val runner = context.system.actorOf(UpdateRunner.props(timeout, botConfig.getString("token")))

  /** Start requesting updates */
  scheduleUpdate(1 second)

  /**
   * Schedule a update, delayed by a given delay
   * @param delay the delay before triggering the update
   * @return cancellable
   */
  def scheduleUpdate(delay: FiniteDuration): Cancellable = {
    context.system.scheduler.scheduleOnce(delay, runner, TriggerUpdate(lastReceivedUpdate getOrElse 0))
  }

  override def receive: Receive = {

    case UpdateResult(updates) =>
      if (updates.nonEmpty) {
        lastReceivedUpdate = Some(updates.map(_.id).max + 1)

        for {
          listener <- updateListeners
          update <- updates
        } yield listener ! NewUpdate(update)
      }
      scheduleUpdate(5 seconds)
  }
}
