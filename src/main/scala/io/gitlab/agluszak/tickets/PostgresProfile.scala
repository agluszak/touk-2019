package io.gitlab.agluszak.tickets

import com.github.tminglei.slickpg.{ExPostgresProfile, PgArraySupport, PgDate2Support}
import pl.iterators.kebs.Kebs

trait PostgresProfile extends ExPostgresProfile with PgArraySupport with PgDate2Support {

  trait API extends super.API with ArrayImplicits with Kebs

  override val api: API = new API {}
}

object PostgresProfile extends PostgresProfile {

}
