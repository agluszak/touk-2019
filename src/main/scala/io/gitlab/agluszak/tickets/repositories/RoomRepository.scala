package io.gitlab.agluszak.tickets.repositories

import io.gitlab.agluszak.tickets.PostgresProfile.api._
import io.gitlab.agluszak.tickets.{Room, RoomId}
import pl.iterators.kebs.tagged.slick.SlickSupport

object RoomRepository extends SlickSupport {

  val rooms = TableQuery[RoomsTable]

  class RoomsTable(tag: Tag) extends Table[Room](tag, "rooms") {
    def id = column[RoomId]("id", O.PrimaryKey, O.AutoInc)

    def name = column[String]("name")

    def width = column[Int]("width")

    def height = column[Int]("height")

    override def * = (id.?, name, width, height).mapTo[Room]

  }

}
