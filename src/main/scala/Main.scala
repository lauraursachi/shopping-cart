

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.lursachi.http.Routes
import com.lursachi.service.ActorSystemAware
import com.lursachi.util.Config
import com.lursachi.util.Constants._

import scala.io.StdIn

object Main extends App with Config with Routes with ActorSystemAware {

  override implicit val system = ActorSystem(actorSystemName)

  override implicit val executionContext = system.dispatcher

  implicit val materializer = ActorMaterializer()

  val bindingFuture = Http().bindAndHandle(handler = routes, httpInterface, httpPort)

  println(s"Server online at http://$httpInterface:$httpPort/\nPress RETURN to stop...")
  StdIn.readLine()

  bindingFuture
    .flatMap(_.unbind()) // trigger unbinding from the port
    .onComplete(_ => system.terminate()) // and shutdown when done
}