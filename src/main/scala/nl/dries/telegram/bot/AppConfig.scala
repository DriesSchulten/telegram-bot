package nl.dries.telegram.bot

import akka.actor.{ExtendedActorSystem, Extension, ExtensionId, ExtensionIdProvider}
import com.typesafe.config.Config

/**
 * Appliciation specific config
 */
class AppConfig(config: Config) extends Extension {

  val token = config.getString("bot.token")

  private val apiBase = config.getString("bot.api.base")

  val timeout = config.getInt("bot.api.timeout")

  lazy val botApi = s"$apiBase$token"

  lazy val getupdates = s"$botApi/getupdates"

  val weatherToken = config.getString("weather.token")

  val weatherApi = config.getString("weather.api")
}

object AppConfig extends ExtensionId[AppConfig] with ExtensionIdProvider {

  override def lookup() = AppConfig

  override def createExtension(system: ExtendedActorSystem): AppConfig = new AppConfig(system.settings.config)
}
