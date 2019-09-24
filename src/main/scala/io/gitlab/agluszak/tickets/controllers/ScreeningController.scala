package io.gitlab.agluszak.tickets.controllers

import java.time.LocalDateTime

import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import io.gitlab.agluszak.tickets.http.responses.ApiError
import io.gitlab.agluszak.tickets.services.ScreeningService
import io.gitlab.agluszak.tickets.{Protocol, ScreeningIdTag}
import pl.iterators.kebs.tagged._

import scala.concurrent.ExecutionContext.Implicits.global

class ScreeningController(screeningService: ScreeningService) extends Protocol {
  val routes: Route =
    pathPrefix("screenings") {
      pathEndOrSingleSlash {
        (get & parameters(('from.as[LocalDateTime], 'to.as[LocalDateTime]))) { (from, to) =>
          complete {
            OK -> screeningService.listScreeningsInInterval(from, to)
          }
        }
      } ~
        pathPrefix(LongNumber) { id =>
          pathEndOrSingleSlash {
            get {
              complete {
                screeningService.get(id.taggedWith[ScreeningIdTag]).map[ToResponseMarshallable] {
                  case Some(details) => OK -> details
                  case None => NotFound -> ApiError("NotFound", s"No screening with id $id", Some(id))
                }
              }
            }
          }
        }
    }
}
