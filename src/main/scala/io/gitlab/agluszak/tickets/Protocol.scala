package io.gitlab.agluszak.tickets

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.unmarshalling.Unmarshaller
import pl.iterators.kebs.json.KebsSpray
import pl.iterators.kebs.tagged._
import spray.json.{DefaultJsonProtocol, _}

import scala.language.implicitConversions
import scala.util.Try

trait Protocol extends SprayJsonSupport with DefaultJsonProtocol with KebsSpray {
  implicit def jsonTaggedFormat[A, T](implicit baseJsonFormat: JsonFormat[A]): JsonFormat[A @@ T] = {
    val reader: JsValue => A @@ T = json => baseJsonFormat.read(json).taggedWith[T]
    val writer: A => JsValue = obj => baseJsonFormat.write(obj)
    jsonFormat[A @@ T](reader, writer)
  }

  implicit val localDateTimeUnmarshaller: Unmarshaller[String, LocalDateTime] =
    Unmarshaller.strict[String, LocalDateTime] { string ⇒
      import java.time.format.DateTimeFormatter
      val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
      val dateTime = LocalDateTime.parse(string, formatter)
      dateTime
    }

  implicit val localDateTimeFormat: JsonFormat[LocalDateTime] = new JsonFormat[LocalDateTime] {
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    private val deserializationErrorMessage =
      s"Expected date time in ISO local date time format, like ${LocalDateTime.now().format(formatter)}"

    override def read(json: JsValue): LocalDateTime = {
      json match {
        case JsString(dateTime) => Try(LocalDateTime.parse(dateTime, formatter))
          .getOrElse(deserializationError(deserializationErrorMessage))
        case _ => deserializationError(deserializationErrorMessage)
      }
    }

    override def write(obj: LocalDateTime): JsValue = JsString(formatter.format(obj))
  }
}
