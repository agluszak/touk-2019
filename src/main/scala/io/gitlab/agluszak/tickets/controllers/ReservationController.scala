package io.gitlab.agluszak.tickets.controllers

import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.model.StatusCodes.{NotFound, OK, _}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import io.gitlab.agluszak.tickets.http.requests.ReservationCreateRequest
import io.gitlab.agluszak.tickets.http.responses.{ApiError, ReservationCreateResponse}
import io.gitlab.agluszak.tickets.services.ReservationService
import io.gitlab.agluszak.tickets.{Protocol, ReservationIdTag}
import pl.iterators.kebs.tagged._
import pl.iterators.kebs.unmarshallers._

import scala.concurrent.ExecutionContext.Implicits.global


class ReservationController(reservationService: ReservationService) extends Protocol {

  val routes: Route =
    pathPrefix("reservations") {
      pathEndOrSingleSlash {
        (post & entity(as[ReservationCreateRequest])) { (request) =>
          complete {
            reservationService.create(request).map[ToResponseMarshallable] {
              case ReservationCreateResponse.AlreadyReserved(seats) =>
                BadRequest ->
                  ApiError("AlreadyReserved", s"Seats ${seats.mkString(", ")} already reserved", Some(seats))
              case ReservationCreateResponse.OrphanedSeat =>
                BadRequest ->
                  ApiError("OrphanedSeat", "There cannot be a single place left over in a row between two already reserved places", Option.empty[String])
              case ReservationCreateResponse.MalformedRequest =>
                BadRequest ->
                  ApiError("MalformedRequest", "Request doesn't make sense", Option.empty[String])
              case ReservationCreateResponse.TooLate =>
                BadRequest ->
                  ApiError("TooLate", "It is to late to make reservations for this screening", Option.empty[String])
              case ReservationCreateResponse.NoSuchScreening(id) =>
                BadRequest ->
                  ApiError("NoSuchScreening", s"No screening with id $id", Some(id))
              case ReservationCreateResponse.InvalidTicketType(id) =>
                BadRequest ->
                  ApiError("InvalidTicketType", s"Ticket type $id is invalid", Some(id))
              case ReservationCreateResponse.NoSeats =>
                BadRequest ->
                  ApiError("NoSeats", "Reservation applies to at least one seat", Option.empty[String])
              case ReservationCreateResponse.InvalidName(name) =>
                BadRequest ->
                  ApiError("InvalidName", s"Name $name is invalid. Name should be at least 3 characters long, starting with a capital letter", Some(name))
              case ReservationCreateResponse.InvalidSurname(surname) =>
                BadRequest ->
                  ApiError("InvalidSurname", s"Surname $surname is invalid. Surname should be at least 3 characters long, starting with a capital letter. It could consist of two parts separated with a single dash, in this case the second part should also start with a capital letter.", Some(surname))
              case details: ReservationCreateResponse.OK =>
                Created ->
                  details
            }
          }
        }
      } ~
        pathPrefix(LongNumber) { id =>
          pathEndOrSingleSlash {
            get {
              complete {
                reservationService.get(id.taggedWith[ReservationIdTag]).map[ToResponseMarshallable] {
                  case Some(details) => OK -> details
                  case None => NotFound -> ApiError("NotFound", s"No reservation with id $id", Some(id))
                }
              }
            }
          }
        }
    }
}
