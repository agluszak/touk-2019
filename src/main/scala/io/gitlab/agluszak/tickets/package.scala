package io.gitlab.agluszak

import java.time.LocalDateTime

import io.gitlab.agluszak.tickets.http.requests.ReservationCreateRequest
import pl.iterators.kebs.tagged._

package object tickets {

  trait MovieIdTag

  type MovieId = Long @@ MovieIdTag

  case class Movie(id: Option[MovieId], title: String)

  trait RoomIdTag

  type RoomId = Long @@ RoomIdTag

  case class Room(id: Option[RoomId], name: String, width: Int, height: Int)

  trait ScreeningIdTag

  type ScreeningId = Long @@ ScreeningIdTag

  case class Screening(id: Option[ScreeningId], movieId: MovieId, roomId: RoomId, time: LocalDateTime)

  case class ScreeningSeat(id: ScreeningId, seat: Int)

  trait TicketTypeIdTag

  type TicketTypeId = Long @@ TicketTypeIdTag

  case class TicketType(id: TicketTypeId, name: String, price: Double)

  trait ReservationIdTag

  type ReservationId = Long @@ ReservationIdTag

  case class Reservation(id: Option[ReservationId], screeningId: ScreeningId, name: String,
                         surname: String, seats: List[Int], created: LocalDateTime)

  object Reservation {
    def fromRequest(id: ReservationId, request: ReservationCreateRequest): Reservation = {
      Reservation(Some(id), request.screeningId, request.name, request.surname, request.seats, LocalDateTime.now())
    }

    def tupled = (Reservation.apply _).tupled
  }

  case class ReservationTickets(reservationId: ReservationId, ticketTypeId: TicketTypeId, quantity: Int)

  case class MovieScreenings(movieTitle: String, screenings: Seq[Screening])

  case class ScreeningDetails(screening: Screening, room: Room, takenSeats: List[Int])

  case class TicketsQuantity(ticketType: TicketTypeId, quantity: Int)

  object TicketsQuantity {
    def fromReservationTickets(reservationTickets: ReservationTickets): TicketsQuantity = {
      TicketsQuantity(reservationTickets.ticketTypeId, reservationTickets.quantity)
    }

    def tupled = (TicketsQuantity.apply _).tupled
  }

  case class ReservationDetails(reservation: Reservation, tickets: List[TicketsQuantity])

}


