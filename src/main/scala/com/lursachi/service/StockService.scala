package com.lursachi.service

import akka.pattern.ask
import com.lursachi.repository.InMemoryProductRepository
import com.lursachi.service.ServiceProtocol._
import com.lursachi.service.Stock.AvailableStock
import com.lursachi.util.Constants._
import com.lursachi.util.Functions._

import scala.concurrent.Future

trait StockService extends ActorSystemAware {

  def getStock: Future[ServiceResponse] = {
    implicit val repo = InMemoryProductRepository
    val stockActor = getStockActor()
    (stockActor ? Get) map {
      case AvailableStock(products) =>
        val res = products.toSeq.collect {
          toBasketProduct
        }
        ActionPerformed(Get, res)
      case _ => InternalError("Error retrieving stock")
    }
  }
}
