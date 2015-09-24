package nl.dries.telegram.bot

import akka.actor.{ExtendedActorSystem, Extension, ExtensionId, ExtensionIdProvider}
import com.typesafe.config.Config

/**
 * Appliciation specific config
 */
class AppConfig(config: Config) extends Extension {

  val token = config.getString("bot.token")

  val apiBase = config.getString("bot.api.base")

  val timeout = config.getInt("bot.api.timeout")

  val getupdates = s"$apiBase$token/getupdates"
}

object AppConfig extends ExtensionId[AppConfig] with ExtensionIdProvider {

  override def lookup() = AppConfig

  override def createExtension(system: ExtendedActorSystem): AppConfig = new AppConfig(system.settings.config)
}
