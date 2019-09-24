import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import io.gitlab.agluszak.tickets.Server
import org.flywaydb.core.Flyway

import scala.concurrent.ExecutionContextExecutor
import scala.io.StdIn

object ManualTest {
  implicit val system: ActorSystem = ActorSystem("my-system")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  def main(args: Array[String]): Unit = {
    val server = new Server()
    val flyway = Flyway.configure.dataSource(server.url, server.user, server.password).load
    flyway.clean()
    flyway.migrate()
    server.start()
    println(s"Test server online at http://${server.interface}:${server.port}/\nPress RETURN to stop...")
    StdIn.readLine()
    server.stop()
    flyway.clean()
  }
}

