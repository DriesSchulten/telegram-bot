package nl.dries.telegram.bot

import java.util.Date

import spray.json._

sealed trait MessageSender {
  def id: Int
}

case class Message(id: Int, date: Date, text: Option[String], chat: Either[User, GroupChat], from: User)

case class Update(id: Int, message: Message)

case class Updates(ok: Boolean, result: List[Update])

case class User(id: Int, firstName: String, lastName: String, username: String) extends MessageSender

case class GroupChat(id: Int, title: String) extends MessageSender

sealed trait SendableMessage {
  def chat: MessageSender
}

case class SendableText(chat: MessageSender, text: String) extends SendableMessage

object TelegramJsonProtocol extends DefaultJsonProtocol {
  implicit object DateJsonFormat extends JsonFormat[Date] {
    override def write(obj: Date): JsValue = JsNumber(obj.getTime / 1000)

    override def read(json: JsValue): Date = json match {
      case JsNumber(value) => new Date(value.toLong * 1000)
      case _ => throw new DeserializationException("Expected value not in epoch")
    }
  }

  implicit val groupChatFormat = jsonFormat2(GroupChat)
  implicit val userFormat = jsonFormat(User, "id", "first_name", "last_name", "username")
  implicit val messageFormat = jsonFormat(Message, "message_id", "date", "text", "chat", "from")
  implicit val updateFormat = jsonFormat(Update, "update_id", "message")
  implicit val updatesFormat = jsonFormat2(Updates)

  implicit object MessageSenderFormat extends RootJsonFormat[MessageSender] {
    override def read(json: JsValue): MessageSender = throw new IllegalStateException("Not possible, write only for generic MessageSender!")

    override def write(obj: MessageSender): JsValue = JsNumber(obj.id)
  }

  implicit val sendableTextFormat = jsonFormat(SendableText, "chat_id", "text")
}
