package io.gitlab.agluszak.tickets.repositories

import io.gitlab.agluszak.tickets.PostgresProfile.api._
import io.gitlab.agluszak.tickets.{TicketType, TicketTypeId, TicketsQuantity}
import pl.iterators.kebs.tagged.slick.SlickSupport

import scala.concurrent.ExecutionContext.Implicits.global

object TicketTypeRepository extends SlickSupport {

  val ticketTypes = TableQuery[TicketTypesTable]

  class TicketTypesTable(tag: Tag) extends Table[TicketType](tag, "ticket_types") {
    def id = column[TicketTypeId]("id", O.PrimaryKey, O.AutoInc)

    def name = column[String]("name")

    def price = column[Double]("price")

    override def * = (id, name, price).mapTo[TicketType]
  }

}

class TicketTypeRepository extends SlickSupport {
  def get(id: TicketTypeId): DBIO[Option[TicketType]] = {
    TicketTypeRepository.ticketTypes.filter(_.id === id).result.headOption
  }

  def total(tickets: List[TicketsQuantity]): DBIO[Double] = {
    DBIO.sequence(
      tickets.map(ticketQuantity => get(ticketQuantity.ticketType)
        .map(ticketTypeOpt =>
          ticketTypeOpt.map(ticketType =>
            ticketType.price * ticketQuantity.quantity)
        )
      )
    ).map(_.flatten.sum)
  }
}