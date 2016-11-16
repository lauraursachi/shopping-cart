package com.lursachi.service

import akka.actor.{ActorRef, ActorSystem}
import akka.testkit.{TestActor, TestProbe}
import com.lursachi.domain.{BasketProduct, BasketProductEntry, Product}
import com.lursachi.http.BasketServiceRoute
import com.lursachi.repository.{InMemoryProductRepository, StockRepository}
import com.lursachi.service.ServiceProtocol._
import com.lursachi.service.Stock.{AddOrReplaceExistingStock, AvailableStock, StockUpdated}

import scala.concurrent.{ExecutionContext, Future}


object TestMocks {

  def getStockActor(repo: StockRepository, myProbe: TestProbe) = {
    myProbe.setAutoPilot(new TestActor.AutoPilot {
      def run(sender: ActorRef, msg: Any) = msg match {
        case Get =>
          sender ! AvailableStock(TestMocks.MockStockRepository.getAll)
          TestActor.KeepRunning
        case AddOrReplaceExistingStock(id, qty) =>
          sender ! StockUpdated
          TestActor.KeepRunning
        case StopActor =>
          TestActor.NoAutoPilot
      }
    })
    myProbe.ref
  }

  object StopActor

  object MockStockRepository extends StockRepository {
    override def getAll: Map[Int, Int] = Map(1 -> 10, 2 -> 20)
  }

  object StockRepositoryWhenProductInBasket extends StockRepository {
    override def getAll: Map[Int, Int] = Map(1 -> 8, 2 -> 20)
  }

  object RouteForEmptyBasket extends BasketServiceRoute() {
    override implicit def system: ActorSystem = ActorSystem("testActorSystem")

    override implicit def executionContext: ExecutionContext = system.dispatcher

    override def perform(action: Action, basketEntryOption: Option[BasketProductEntry]) = Future.successful(ActionPerformed(Get, Seq.empty))
  }

  object RouteForOneProductInBasket extends BasketServiceRoute() {
    override implicit def system: ActorSystem = ActorSystem("testActorSystem")

    override implicit def executionContext: ExecutionContext = system.dispatcher

    override def perform(action: Action, basketEntryOption: Option[BasketProductEntry]): Future[ServiceResponse] =
      action match {
        case Get => Future.successful(
          ActionPerformed(Get, Seq(BasketProduct(Product(1, "Apple", "apples", BigDecimal("9.99")), 5))))

        case Update => Future.successful(
          ActionPerformed(Update, Seq(BasketProduct(
            InMemoryProductRepository.find(basketEntryOption.get.productId).get, basketEntryOption.get.quantity))))
      }


  }

  object RouteForExistingProductInBasket extends BasketServiceRoute() {
    override implicit def system: ActorSystem = ActorSystem("testActorSystem")

    override implicit def executionContext: ExecutionContext = system.dispatcher

    override def perform(action: Action, basketEntry: Option[BasketProductEntry]): Future[ServiceResponse] =
      Future.successful(ActionPerformed(Delete, 1))

  }

  object RouteForNonExistingProductInBasket extends BasketServiceRoute() {
    override implicit def system: ActorSystem = ActorSystem("testActorSystem")

    override implicit def executionContext: ExecutionContext = system.dispatcher

    override def perform(action: Action, basketEntry: Option[BasketProductEntry]): Future[ServiceResponse] =
      Future.successful(ProductNotFound(1))

  }

  object TestedRoute extends BasketServiceRoute() {
    override implicit def system: ActorSystem = ActorSystem("testActorSystem")

    override implicit def executionContext: ExecutionContext = system.dispatcher
  }

}
