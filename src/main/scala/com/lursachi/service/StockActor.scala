package com.lursachi.service

import akka.actor.{Actor, Props}
import com.lursachi.repository.StockRepository
import com.lursachi.service.ServiceProtocol.Get
import com.lursachi.service.Stock._

import scala.concurrent.ExecutionContext

class StockActor(stockRepository: StockRepository)(implicit val ec: ExecutionContext) extends Actor with State {

  override def receive: Receive = currentStock(stockRepository.getAll)

  def currentStock(stocks: Map[Int, Int]): Receive = {
    case Get =>
      sender ! AvailableStock(stocks)
    case AddOrReplaceExistingStock(id, qty) =>
      context become currentStock(addOrReplaceExisting(stocks, id, qty))
      sender ! StockUpdated
    case AddToExistingStock(id, qty) =>
      context become currentStock(addToExisting(stocks, id, qty))
      sender ! StockUpdated
  }
}

object StockActor {
  def props(stockRepo: StockRepository)(implicit ec: ExecutionContext) = Props(new StockActor(stockRepo))
}

