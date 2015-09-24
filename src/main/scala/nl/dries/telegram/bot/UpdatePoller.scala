package nl.dries.telegram.bot

import akka.actor.{Actor, ActorRef, Cancellable, Props}
import spray.http.Uri

import scala.concurrent.duration._

object UpdatePoller {

  def props() = Props(classOf[UpdatePoller])

  case class NewUpdate(update: Update)
}

class UpdatePoller extends Actor {

  import UpdatePoller._
  import UpdateRunner._
  import context.dispatcher

  /** Config */
  val appConfig = AppConfig(context.system)
  /** Runner actor */
  val runner = context.actorOf(UpdateRunner.props(appConfig.timeout, Uri(appConfig.getupdates)))
  /** Last received counter */
  var lastReceivedUpdate: Option[Int] = None
  /** Set of listeners to updates */
  var updateListeners = Set.empty[ActorRef]

  /** Start requesting updates */
  scheduleUpdate(1.second)

  override def receive: Receive = {

    case UpdateResult(updates) =>
      if (updates.nonEmpty) {
        lastReceivedUpdate = Some(updates.map(_.id).max + 1)

        for {
          listener <- updateListeners
          update <- updates
        } yield listener ! NewUpdate(update)
      }
      scheduleUpdate(5.seconds)
  }

  /**
   * Schedule a update, delayed by a given delay
   * @param delay the delay before triggering the update
   * @return cancellable
   */
  def scheduleUpdate(delay: FiniteDuration): Cancellable = {
    context.system.scheduler.scheduleOnce(delay, runner, TriggerUpdate(lastReceivedUpdate getOrElse 0))
  }
}
