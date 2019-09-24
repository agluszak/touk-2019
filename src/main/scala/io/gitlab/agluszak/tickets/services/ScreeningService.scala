package io.gitlab.agluszak.tickets.services

import java.time.LocalDateTime

import io.gitlab.agluszak.tickets.PostgresProfile.api._
import io.gitlab.agluszak.tickets.repositories.ScreeningRepository
import io.gitlab.agluszak.tickets.{MovieScreenings, ScreeningDetails, ScreeningId}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ScreeningService(db: Database, screeningRepository: ScreeningRepository) {
  def listScreeningsInInterval(from: LocalDateTime, to: LocalDateTime): Future[Seq[MovieScreenings]] =
    db.run(
      screeningRepository.listScreeningsInInterval(from, to).map(list =>
        list.groupBy(_._1)
          .map(pair => MovieScreenings(pair._1.title, pair._2.map(_._2)))
          .toSeq
          .sortBy(_.movieTitle)
      )
    )

  def get(id: ScreeningId): Future[Option[ScreeningDetails]] =
    db.run(screeningRepository.getWithRoomAndSeats(id))
}
