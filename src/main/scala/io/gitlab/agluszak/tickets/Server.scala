package io.gitlab.agluszak.tickets

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import io.gitlab.agluszak.tickets.PostgresProfile.api._
import io.gitlab.agluszak.tickets.controllers.{ReservationController, ScreeningController}
import io.gitlab.agluszak.tickets.repositories.{ReservationRepository, ScreeningRepository, TicketTypeRepository}
import io.gitlab.agluszak.tickets.services.{ReservationService, ScreeningService}

import scala.concurrent.{ExecutionContextExecutor, Future}

class Server(implicit system: ActorSystem, materializer: ActorMaterializer, executor: ExecutionContextExecutor) {
  val db: Database = Database.forConfig("postgres")
  val config = ConfigFactory.load()
  val interface = config.getString("app.interface")
  val port = config.getString("app.port").toInt
  val user = config.getString("postgres.user")
  val password = config.getString("postgres.password")
  val url = config.getString("postgres.url")

  // Repositories
  val screeningRepository = new ScreeningRepository
  val reservationRepository = new ReservationRepository
  val ticketTypeRepository = new TicketTypeRepository

  // Services
  val screeningService = new ScreeningService(db, screeningRepository)
  val reservationService = new ReservationService(db, reservationRepository, screeningRepository, ticketTypeRepository)

  // Controllers
  val screeningController = new ScreeningController(screeningService)
  val reservationController = new ReservationController(reservationService)

  val routes = screeningController.routes ~ reservationController.routes

  var binding: Future[Http.ServerBinding] = _

  import io.gitlab.agluszak.tickets.http.JsonRejectionHandler.jsonRejectionHandler

  def start() {
    binding = Http().bindAndHandle(routes, interface, port)
  }

  def stop() {
    binding
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())
  }
}
