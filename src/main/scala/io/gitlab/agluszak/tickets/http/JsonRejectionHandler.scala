package io.gitlab.agluszak.tickets.http

import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse}
import akka.http.scaladsl.server.RejectionHandler

object JsonRejectionHandler {
  implicit val jsonRejectionHandler: RejectionHandler =
    RejectionHandler.default
      .mapRejectionResponse {
        case res@HttpResponse(_, _, ent: HttpEntity.Strict, _) =>
          val message = ent.data.utf8String.replaceAll("\"", """\"""")
          res.copy(entity = HttpEntity(ContentTypes.`application/json`, s"""{"rejection": "$message"}"""))
        case x => x
      }
}
