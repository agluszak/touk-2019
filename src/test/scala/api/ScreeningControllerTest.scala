package api

import akka.http.scaladsl.model.StatusCodes
import io.gitlab.agluszak.tickets.{MovieScreenings, ScreeningDetails}

class ScreeningControllerTest extends ApiTest {

  import server._

  "ScreeningController" can {
    "list screenings" which {
      "returns screenings if there are any" in {
        Get("/screenings?from=2119-09-13T10:00&to=2119-09-13T11:00") ~> routes ~> check {
          status mustBe StatusCodes.OK
          val apiResponse = responseAs[List[MovieScreenings]]
          apiResponse.length mustBe 1
          apiResponse.head.movieTitle mustBe "Straszny film"
          apiResponse.head.screenings.length mustBe 2
        }
      }
    }

    "get screening details" which {
      "succedes if the screening exists" in {
        Get("/screenings/1") ~> routes ~> check {
          status mustBe StatusCodes.OK
          val apiResponse = responseAs[ScreeningDetails]
          apiResponse.room.name mustBe "Dark room"
          apiResponse.screening.movieId mustBe 1
        }
      }

      "fails if the screening doesn't exist" in {
        Get("/screenings/9999") ~> routes ~> check {
          status mustBe StatusCodes.NotFound
        }
      }
    }
  }
}
