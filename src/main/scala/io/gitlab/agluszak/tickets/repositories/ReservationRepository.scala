package io.gitlab.agluszak.tickets.repositories

import java.time.LocalDateTime

import io.gitlab.agluszak.tickets.PostgresProfile.api._
import io.gitlab.agluszak.tickets._
import io.gitlab.agluszak.tickets.http.requests.ReservationCreateRequest
import pl.iterators.kebs.tagged.slick.SlickSupport

import scala.concurrent.ExecutionContext.Implicits.global

object ReservationRepository extends SlickSupport {

  val reservations = TableQuery[ReservationsTable]

  val reservationTickets = TableQuery[ReservationTicketsTable]

  class ReservationTicketsTable(tag: Tag) extends Table[ReservationTickets](tag, "reservation_tickets") {
    def reservationId = column[ReservationId]("reservation_id")

    def ticketTypeId = column[TicketTypeId]("ticket_type_id")

    def quantity = column[Int]("quantity")

    override def * = (reservationId, ticketTypeId, quantity).mapTo[ReservationTickets]
  }

  class ReservationsTable(tag: Tag) extends Table[Reservation](tag, "reservations") {
    def id = column[ReservationId]("id", O.PrimaryKey, O.AutoInc)

    def screeningId = column[ScreeningId]("screening_id")

    def name = column[String]("name")

    def surname = column[String]("surname")

    def seats = column[List[Int]]("seats")

    def created = column[LocalDateTime]("created")

    override def * = (id.?, screeningId, name, surname, seats, created).mapTo[Reservation]
  }

}

class ReservationRepository extends SlickSupport {
  def getWithTickets(id: ReservationId): DBIO[Option[ReservationDetails]] = {
    ReservationRepository.reservations.filter(_.id === id).result
      .zip(ReservationRepository.reservationTickets.filter(_.reservationId === id).result)
      .map { case (reservations, tickets) =>
        reservations.headOption.map(ReservationDetails(_, tickets.map(TicketsQuantity.fromReservationTickets).toList))
      }
  }

  def save(request: ReservationCreateRequest): DBIO[ReservationId] = {
    val reservation = Reservation(None, request.screeningId, request.name,
      request.surname, request.seats, LocalDateTime.now())

    val seats = request.seats.map(ScreeningSeat(request.screeningId, _))

    ((ReservationRepository.reservations returning ReservationRepository.reservations.map(_.id)) += reservation)
      .zip(ScreeningRepository.screeningSeats ++= seats).map(_._1).flatMap { reservationId =>
      val tickets = request.tickets.map(ticketQuantity =>
        ReservationTickets(reservationId, ticketQuantity.ticketType, ticketQuantity.quantity))
      ReservationRepository.reservationTickets.++=(tickets).map(_ => reservationId)
    }.transactionally
  }
}