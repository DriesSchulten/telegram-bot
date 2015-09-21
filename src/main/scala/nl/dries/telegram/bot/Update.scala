package nl.dries.telegram.bot

import spray.json.DefaultJsonProtocol

case class Message(id: Int, date: Int, text: Option[String])

case class Update(id: Int, message: Message)

case class Updates(ok: Boolean, result: List[Update])

object TelegramJsonProtocol extends DefaultJsonProtocol {
  implicit val messageFormat = jsonFormat(Message, "message_id", "date", "text")
  implicit val updateFormat = jsonFormat(Update, "update_id", "message")
  implicit val updatesFormat = jsonFormat2(Updates)
}
