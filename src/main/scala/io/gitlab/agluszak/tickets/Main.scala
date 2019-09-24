package io.gitlab.agluszak.tickets

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import org.flywaydb.core.Flyway

import scala.concurrent.ExecutionContextExecutor
import scala.io.StdIn

object Main {
  implicit val system: ActorSystem = ActorSystem("my-system")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  def main(args: Array[String]) {
    val server = new Server
    val flyway = Flyway.configure.dataSource(server.url, server.user, server.password).load
    flyway.migrate()
    val bindingFuture = server.start()
    println(s"Server online at http://${server.interface}:${server.port}/\nPress RETURN to stop...")
    StdIn.readLine()
    server.stop()
  }
}