package api

import akka.http.scaladsl.testkit.ScalatestRouteTest
import io.gitlab.agluszak.tickets.{Protocol, Server}
import org.flywaydb.core.Flyway
import org.scalatest.{AsyncWordSpec, BeforeAndAfterAll, Inside, MustMatchers}

trait ApiTest extends AsyncWordSpec
  with BeforeAndAfterAll
  with MustMatchers
  with Inside
  with Protocol
  with ScalatestRouteTest {

  import akka.http.scaladsl.testkit.RouteTestTimeout
  import akka.testkit.TestDuration

  import scala.concurrent.duration._

  implicit val timeout = RouteTestTimeout(5.seconds.dilated)

  val server = new Server()
  val flyway = Flyway.configure.dataSource(server.url, server.user, server.password).load

  override def beforeAll: Unit = {
    flyway.clean()
    flyway.migrate()
    server.start()
  }

  override def afterAll(): Unit = {
    server.stop()
    flyway.clean()
  }

}
