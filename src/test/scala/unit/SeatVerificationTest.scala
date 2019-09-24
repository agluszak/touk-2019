package unit

import org.scalatest.{MustMatchers, WordSpec}

class SeatVerificationTest extends WordSpec with MustMatchers {

  import io.gitlab.agluszak.tickets.services.SeatVerification._

  "SeatVerification" should {
    "return true for an empty room and a correct reservation" in {
      verifySeatsReservation(List(0, 1, 2), 3, 1, List.empty) mustBe true
      verifySeatsReservation(List(0, 1, 2), 5, 1, List.empty) mustBe true
      verifySeatsReservation(List(4), 5, 1, List.empty) mustBe true
      verifySeatsReservation(List(0, 1, 2), 2, 2, List.empty) mustBe true
      verifySeatsReservation(List(0, 2), 2, 2, List.empty) mustBe true
    }

    "return false for an empty room and a reservation with a single seat left" in {
      verifySeatsReservation(List(0, 2), 3, 1, List.empty) mustBe false
      verifySeatsReservation(List(0, 2), 5, 1, List.empty) mustBe false
      verifySeatsReservation(List(0, 2, 3, 4), 5, 1, List.empty) mustBe false
    }

    "return false for an empty room anda  reservation where seats do not form an interval in a row" in {
      verifySeatsReservation(List(0, 1, 2, 5, 6), 8, 1, List.empty) mustBe false
    }

    "return true for an empty room and reservation spanning several rows" in {
      verifySeatsReservation(List(0, 1, 3, 4, 5, 6), 3, 3, List.empty) mustBe true
    }

    "return true for some seats reserved and a correct reservation in a single row" in {
      verifySeatsReservation(List(0, 1, 2), 10, 1, List(3, 4, 5)) mustBe true
    }

    "return false for some seats reserved and a reservation leaving a single seat left" in {
      verifySeatsReservation(List(0, 1, 2), 10, 1, List(4, 5)) mustBe false
      verifySeatsReservation(List(4, 5, 6), 10, 1, List(1, 2, 8)) mustBe false
    }

    "return true for some seats reserved and a correct reservation spanning several rows" in {
      verifySeatsReservation(List(0, 1, 2, 22, 33, 34, 35, 36), 10, 5, List(3, 4, 5, 15, 16, 17)) mustBe true
    }
  }
}
