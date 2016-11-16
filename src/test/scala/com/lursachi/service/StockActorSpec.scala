package com.lursachi.service

import akka.actor.SupervisorStrategy.Stop
import com.lursachi.repository.InMemoryStockRepository
import com.lursachi.service.ServiceProtocol.Get
import com.lursachi.service.Stock._
import org.scalatest.{MustMatchers, WordSpecLike}


class StockActorSpec extends ActorSpecBase
  with WordSpecLike
  with MustMatchers {

  "StockActorSpec" when {

    "receives GetStock message" should {
      "respond with AvailableStock message" in {
        val stockActor = system.actorOf(StockActor.props(InMemoryStockRepository), "Stock1")

        stockActor ! Get

        expectMsg(AvailableStock(InMemoryStockRepository.getAll))
        stockActor ! Stop
      }
    }

    "receives AddOrReplaceExistingStock message for an existing stock" should {
      "respond with StockUpdated message and replace its value" in {
        val stockActor = system.actorOf(StockActor.props(InMemoryStockRepository), "Stock2")

        stockActor ! AddOrReplaceExistingStock(1, 4)

        expectMsg(StockUpdated)
        stockActor ! Get
        expectMsg(AvailableStock(Map(1 -> 4, 2 -> 20)))
        stockActor ! Stop
      }
    }

    "receives AddOrReplaceExistingStock message for a new product" should {
      "respond with StockUpdated message and replace its value" in {
        val stockActor = system.actorOf(StockActor.props(InMemoryStockRepository), "Stock3")

        stockActor ! AddOrReplaceExistingStock(3, 6)

        expectMsg(StockUpdated)
        stockActor ! Get
        expectMsg(AvailableStock(Map(1 -> 10, 2 -> 20, 3 -> 6)))
        stockActor ! Stop
      }
    }

    "receives AddToExisting message for an existing product" should {
      "respond with StockUpdated message and add to its value" in {
        val stockActor = system.actorOf(StockActor.props(InMemoryStockRepository), "Stock4")

        stockActor ! AddToExistingStock(2, 12)

        expectMsg(StockUpdated)
        stockActor ! Get
        expectMsg(AvailableStock(Map(1 -> 10, 2 -> 32)))
        stockActor ! Stop
      }
    }
  }
}
