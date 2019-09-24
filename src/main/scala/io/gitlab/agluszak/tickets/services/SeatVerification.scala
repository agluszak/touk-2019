package io.gitlab.agluszak.tickets.services

import scala.collection.immutable.TreeSet

object SeatVerification {
  /// Can verify only non-overlapping seats
  def verifySeatsReservation(reservation: List[Int], roomWidth: Int, roomHeight: Int, alreadyReserved: List[Int]): Boolean = {
    val reservationRows = reservation.groupBy(_ / roomWidth)
    val reservedRows = alreadyReserved.groupBy(_ / roomWidth)

    (0 until roomHeight).forall { i =>
      val reservationRow = reservationRows.getOrElse(i, List.empty)
      val reservedRow = reservedRows.getOrElse(i, List.empty)
      verifyRow(reservationRow, roomWidth, reservedRow)
    }
  }

  private def verifyRow(reservation: List[Int], roomWidth: Int, alreadyReserved: List[Int]): Boolean = {
    if (reservation.isEmpty) {
      true
    } else {
      val firstSeat = reservation.min
      val lastSeat = reservation.max
      // 1. Check whether seat numbers form an interval
      if (lastSeat - firstSeat + 1 != reservation.length) {
        false
      } else {
        // 2. Make sure there are no single seats left on either side
        val sortedReserved = TreeSet.empty[Int] ++ alreadyReserved
        val lowerBoundOpt = sortedReserved.to(firstSeat).lastOption
        val upperBoundOpt = sortedReserved.from(lastSeat).headOption
        (lowerBoundOpt, upperBoundOpt) match {
          case (Some(lower), Some(upper)) => lower != firstSeat - 2 && upper != lastSeat + 2
          case (Some(lower), None) => lower != firstSeat - 2
          case (None, Some(upper)) => upper != lastSeat + 2
          case (None, None) => true
        }
      }
    }
  }
}
