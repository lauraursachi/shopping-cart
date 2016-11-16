package com.lursachi.http

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.unmarshalling.{FromEntityUnmarshaller, PredefinedFromEntityUnmarshallers}
import com.lursachi.domain.BasketProductEntry
import com.lursachi.http.HttpResponseConverter.toHttpResponse
import com.lursachi.service.BasketService
import com.lursachi.service.ServiceProtocol.{Add, Delete, Get, Update}

trait BasketServiceRoute extends BasketService with BasketSerializers {

  implicit val rawIntFromEntityUnmarshaller: FromEntityUnmarshaller[Int] =
    PredefinedFromEntityUnmarshallers.stringUnmarshaller.map(_.toInt)

  val basketRoute = pathPrefix("basket") {
    pathEndOrSingleSlash {
      get {
        complete(perform(Get, None) map toHttpResponse)
      } ~
        post {
          entity(as[BasketProductEntry]) { basketEntry =>
            complete(perform(Add, Some(basketEntry)) map toHttpResponse)
          }
        }
    } ~
      pathPrefix(IntNumber) { productId =>
        pathEndOrSingleSlash {
          put {
            entity(as[Int]) { qty =>
              val updateResult = perform(Update, Some(BasketProductEntry(productId, qty)))
              complete(updateResult map toHttpResponse)
            }
          } ~
            delete {
              val deleteResult = perform(Delete, Some(BasketProductEntry(productId)))
              complete(deleteResult map toHttpResponse)
            }
        }
      }
  }
}

