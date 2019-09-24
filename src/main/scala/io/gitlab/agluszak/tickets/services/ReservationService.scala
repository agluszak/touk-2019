package io.gitlab.agluszak.tickets.services

import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

import io.gitlab.agluszak.tickets.PostgresProfile.api._
import io.gitlab.agluszak.tickets._
import io.gitlab.agluszak.tickets.http.requests.ReservationCreateRequest
import io.gitlab.agluszak.tickets.http.responses.ReservationCreateResponse
import io.gitlab.agluszak.tickets.http.responses.ReservationCreateResponse._
import io.gitlab.agluszak.tickets.repositories.{ReservationRepository, ScreeningRepository, TicketTypeRepository}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ReservationService(db: Database, reservationRepository: ReservationRepository,
                         screeningRepository: ScreeningRepository, ticketTypeRepository: TicketTypeRepository
                        ) {

  import NameVerification._
  import SeatVerification._

  //TODO config
  val reservationValidUntilMinutes = 15

  /// Returns first missing ticket ID or gets total cost of tickets
  private def verifyTicketsAndGetTotal(tickets: List[TicketsQuantity]): DBIO[Either[TicketTypeId, Double]] = {
    DBIO.sequence(tickets.map(ticketsQuantity =>
      ticketTypeRepository.get(ticketsQuantity.ticketType).map {
        ticketTypeOpt =>
          (ticketTypeOpt, ticketsQuantity)
      }
    )).map { list =>
      list.find(_._1.isEmpty) match {
        case Some((_, ticketsQuantity)) => Left(ticketsQuantity.ticketType)
        case None =>
          val cost = list.flatMap { typeQuantityPair =>
            typeQuantityPair._1.map(_.price * typeQuantityPair._2.quantity)
          }.sum
          Right(cost)
      }
    }
  }

  private def checkRoomScreeningAndTime(request: ReservationCreateRequest): DBIO[ReservationCreateResponse] = {
    verifyTicketsAndGetTotal(request.tickets).flatMap[ReservationCreateResponse, NoStream, Effect.All] {
      case Left(invalidTicketType) => DBIO.successful(InvalidTicketType(invalidTicketType))
      case Right(total) => {
        screeningRepository.getWithRoomAndSeats(request.screeningId).flatMap {
          case None => DBIO.successful(NoSuchScreening(request.screeningId))
          case Some(ScreeningDetails(screening, room, reservedSeats)) => {
            val timeDiff = ChronoUnit.MINUTES.between(LocalDateTime.now(), screening.time)
            if (timeDiff <= reservationValidUntilMinutes) {
              DBIO.successful(TooLate)
            } else {
              checkSeatsAndReserve(request, room, reservedSeats.toSet, screening, total)
            }
          }
        }
      }
    }
  }

  /// The final step
  private def checkSeatsAndReserve(request: ReservationCreateRequest, room: Room,
                                   reservedSeats: Set[Int], screening: Screening,
                                   total: Double): DBIO[ReservationCreateResponse] = {
    if (request.seats.max > room.height * room.width || request.seats.min < 0) {
      DBIO.successful(MalformedRequest)
    } else {
      val overlappingSeats = request.seats.toSet & reservedSeats
      if (overlappingSeats.nonEmpty) {
        DBIO.successful(AlreadyReserved(overlappingSeats.toList))
      } else if (!verifySeatsReservation(request.seats, room.width, room.height, reservedSeats.toList)) {
        DBIO.successful(OrphanedSeat)
      } else {
        reservationRepository.save(request).map { reservationId =>
          val reservation = Reservation.fromRequest(reservationId, request)
          val expires = screening.time.minus(reservationValidUntilMinutes, ChronoUnit.MINUTES)
          OK(reservation, total, expires)
        }
      }
    }
  }

  def create(request: ReservationCreateRequest): Future[ReservationCreateResponse] = {
    // simplest checks not requiring to hit the db
    if (!verifyName(request.name)) {
      Future.successful(InvalidName(request.name))
    } else if (!verifySurname(request.surname)) {
      Future.successful(InvalidSurname(request.surname))
    } else if (request.seats.isEmpty) {
      Future.successful(NoSeats)
    } else if (request.seats.length != request.tickets.map(_.quantity).sum) {
      Future.successful(MalformedRequest)
    } else {
      db.run(checkRoomScreeningAndTime(request))
    }
  }

  def get(id: ReservationId): Future[Option[ReservationDetails]] = {
    db.run(reservationRepository.getWithTickets(id))
  }
}

