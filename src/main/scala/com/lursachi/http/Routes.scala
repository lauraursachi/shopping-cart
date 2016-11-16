package com.lursachi.http

import akka.http.scaladsl.server.Directives._

trait Routes extends BasketServiceRoute with StockServiceRoute {

  val routes =
    pathPrefix("user") {
      basketRoute
    } ~ stocksRoute

}
