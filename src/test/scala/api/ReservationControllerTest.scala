package api

import akka.http.scaladsl.model.StatusCodes
import io.gitlab.agluszak.tickets.http.requests.ReservationCreateRequest
import io.gitlab.agluszak.tickets.http.responses.{ApiError, ReservationCreateResponse}
import io.gitlab.agluszak.tickets.{ScreeningIdTag, TicketTypeIdTag, TicketsQuantity}
import org.scalactic.{Equality, TolerantNumerics}
import pl.iterators.kebs.tagged._

class ReservationControllerTest extends ApiTest {

  import server._

  "ReservationController" can {
    "create reservations" which {
      val farFutureScreeningId = 1L.taggedWith[ScreeningIdTag]
      val tooLateScreeningId = 6L.taggedWith[ScreeningIdTag]
      val nonExistentScreeningId = 666L.taggedWith[ScreeningIdTag]

      val normalTicketTypeId = 1L.taggedWith[TicketTypeIdTag]
      val nonExistentTicketTypeId = 666L.taggedWith[TicketTypeIdTag]
      "succeeds when given correct request" in {
        implicit val doubleEquality: Equality[Double] = TolerantNumerics.tolerantDoubleEquality(0.01)
        val request = ReservationCreateRequest(farFutureScreeningId,
          "Andrzej", "Głuszak", List(0), List(TicketsQuantity(normalTicketTypeId, 1)))
        Post("/reservations", request) ~> routes ~> check {
          status mustBe StatusCodes.Created
          val apiResponse = responseAs[ReservationCreateResponse.OK]
          apiResponse.total must ===(25D)
          apiResponse.reservation.screeningId mustBe request.screeningId
          apiResponse.reservation.name mustBe request.name
          apiResponse.reservation.surname mustBe request.surname
          apiResponse.reservation.seats mustBe request.seats
          apiResponse.reservation.id mustBe defined
        }
      }

      "fails when no seats are being reserved" in {
        val request = ReservationCreateRequest(farFutureScreeningId,
          "Andrzej", "Głuszak", List(), List())
        Post("/reservations", request) ~> routes ~> check {
          status mustBe StatusCodes.BadRequest
          val apiResponse = responseAs[ApiError[String]]
          apiResponse.errorType mustBe "NoSeats"
        }
      }

      "fails when trying to reserve seats for a screening which doesn't exist" in {
        val request = ReservationCreateRequest(nonExistentScreeningId,
          "Andrzej", "Głuszak", List(0), List(TicketsQuantity(normalTicketTypeId, 1)))
        Post("/reservations", request) ~> routes ~> check {
          status mustBe StatusCodes.BadRequest
          val apiResponse = responseAs[ApiError[Int]]
          apiResponse.errorType mustBe "NoSuchScreening"
          apiResponse.param mustBe Some(nonExistentScreeningId)
        }
      }

      "fails when trying to buy tickets of wrong type" in {
        val request = ReservationCreateRequest(farFutureScreeningId,
          "Andrzej", "Głuszak", List(0), List(TicketsQuantity(nonExistentTicketTypeId, 1)))
        Post("/reservations", request) ~> routes ~> check {
          status mustBe StatusCodes.BadRequest
          val apiResponse = responseAs[ApiError[Int]]
          apiResponse.errorType mustBe "InvalidTicketType"
          apiResponse.param mustBe Some(nonExistentTicketTypeId)
        }
      }

      // This one should use a mock in order to be 100% correct, but let's make an assumption
      // that no one will put their clock back more than a few hours ;)
      "fails when it is too late to make a reservation" in {
        val request = ReservationCreateRequest(tooLateScreeningId,
          "Andrzej", "Głuszak", List(0), List(TicketsQuantity(normalTicketTypeId, 1)))
        Post("/reservations", request) ~> routes ~> check {
          status mustBe StatusCodes.BadRequest
          val apiResponse = responseAs[ApiError[String]]
          apiResponse.errorType mustBe "TooLate"
          apiResponse.param mustBe None
        }
      }

      "fail when trying to reserve already reserved seats" in {
        implicit val doubleEquality: Equality[Double] = TolerantNumerics.tolerantDoubleEquality(0.01)
        val request = ReservationCreateRequest(farFutureScreeningId,
          "Andrzej", "Głuszak", List(1), List(TicketsQuantity(normalTicketTypeId, 1)))
        Post("/reservations", request) ~> routes ~> check {
          status mustBe StatusCodes.Created
          val apiResponse = responseAs[ReservationCreateResponse.OK]
          apiResponse.reservation.id mustBe defined
        }
        Post("/reservations", request) ~> routes ~> check {
          status mustBe StatusCodes.BadRequest
          val apiResponse = responseAs[ApiError[List[Int]]]
          apiResponse.errorType mustBe "AlreadyReserved"
          apiResponse.param mustBe Some(List(1))
        }
      }
    }
  }
}
