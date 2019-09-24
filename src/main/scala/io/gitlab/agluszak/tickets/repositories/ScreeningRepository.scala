package io.gitlab.agluszak.tickets.repositories

import java.time.LocalDateTime

import io.gitlab.agluszak.tickets.PostgresProfile.api._
import io.gitlab.agluszak.tickets.{ScreeningDetails, _}
import pl.iterators.kebs.tagged.slick.SlickSupport

import scala.concurrent.ExecutionContext.Implicits.global

object ScreeningRepository extends SlickSupport {

  val screenings = TableQuery[ScreeningsTable]

  val screeningSeats = TableQuery[ScreeningSeatsTable]

  class ScreeningSeatsTable(tag: Tag) extends Table[ScreeningSeat](tag, "screening_seats") {
    def screeningId = column[ScreeningId]("screening_id")

    def seat = column[Int]("seat")

    override def * = (screeningId, seat).mapTo[ScreeningSeat]
  }

  class ScreeningsTable(tag: Tag) extends Table[Screening](tag, "screenings") {
    def id = column[ScreeningId]("id", O.PrimaryKey, O.AutoInc)

    def movieId = column[MovieId]("movie_id")

    def roomId = column[RoomId]("room_id")

    def time = column[LocalDateTime]("time")

    override def * = (id.?, movieId, roomId, time).mapTo[Screening]
  }

}

class ScreeningRepository extends SlickSupport {

  def listScreeningsInInterval(from: LocalDateTime, to: LocalDateTime): DBIO[Seq[(Movie, Screening)]] = {
    val filteredScreenings = ScreeningRepository.screenings
      .filter(screening => screening.time >= from && screening.time <= to)
    MovieRepository.movies
      .join(filteredScreenings)
      .on(_.id === _.movieId)
      .sortBy(pair => (pair._1.title, pair._2.time))
      .result
  }

  def getWithRoomAndSeats(id: ScreeningId): DBIO[Option[ScreeningDetails]] =
    ScreeningRepository.screenings
      .filter(_.id === id)
      .join(RoomRepository.rooms)
      .on(_.roomId === _.id)
      .result
      .zip(ScreeningRepository.screeningSeats.filter(_.screeningId === id).map(_.seat).result)
      .map { case (screeningRoomPair, seats) =>
        screeningRoomPair.headOption.map {
          case (screening, room) => ScreeningDetails(screening, room, seats.toList)
        }
      }
}
