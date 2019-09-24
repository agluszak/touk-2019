package io.gitlab.agluszak.tickets.http.responses

import java.time.LocalDateTime

import io.gitlab.agluszak.tickets.{Reservation, ScreeningId, TicketTypeId}

sealed trait ReservationCreateResponse extends Product with Serializable

object ReservationCreateResponse {

  case class OK(reservation: Reservation, total: Double, expires: LocalDateTime) extends ReservationCreateResponse

  case object TooLate extends ReservationCreateResponse

  case class NoSuchScreening(id: ScreeningId) extends ReservationCreateResponse

  case class InvalidName(name: String) extends ReservationCreateResponse

  case class InvalidSurname(surname: String) extends ReservationCreateResponse

  case object NoSeats extends ReservationCreateResponse

  case object OrphanedSeat extends ReservationCreateResponse

  case class AlreadyReserved(seats: List[Int]) extends ReservationCreateResponse

  case class InvalidTicketType(ticketType: TicketTypeId) extends ReservationCreateResponse

  case object MalformedRequest extends ReservationCreateResponse

}
