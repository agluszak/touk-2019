package io.gitlab.agluszak.tickets.repositories

import io.gitlab.agluszak.tickets.PostgresProfile.api._
import io.gitlab.agluszak.tickets.{Movie, MovieId}
import pl.iterators.kebs.tagged.slick.SlickSupport

object MovieRepository extends SlickSupport {

  val movies = TableQuery[MoviesTable]

  class MoviesTable(tag: Tag) extends Table[Movie](tag, "movies") {
    def id = column[MovieId]("id", O.PrimaryKey, O.AutoInc)

    def title = column[String]("title")

    override def * = (id.?, title).mapTo[Movie]

  }

}