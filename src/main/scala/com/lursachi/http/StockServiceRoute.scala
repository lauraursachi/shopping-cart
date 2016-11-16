package com.lursachi.http

import akka.http.scaladsl.server.Directives._
import com.lursachi.http.HttpResponseConverter.toHttpResponse
import com.lursachi.service.StockService

trait StockServiceRoute extends StockService with BasketSerializers {

  val stocksRoute = pathPrefix("stock") {
    pathEndOrSingleSlash {
      get {
        complete(getStock map toHttpResponse)
      }
    }
  }
}

