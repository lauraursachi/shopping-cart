package com.lursachi.service

import akka.actor.ActorRef
import akka.actor.SupervisorStrategy.Stop
import akka.testkit.{TestKit, TestProbe}
import com.lursachi.domain.BasketProductEntry
import com.lursachi.service.Basket._
import com.lursachi.service.ServiceProtocol.{Add, Get, Update}
import com.lursachi.service.Stock.AddOrReplaceExistingStock
import com.lursachi.service.TestMocks.{MockStockRepository, StopActor}
import org.scalatest.{MustMatchers, WordSpecLike}

class BasketActorSpec extends ActorSpecBase
  with WordSpecLike
  with MustMatchers {

  "BasketActor" when {

    "receives Get message for an empty basket" should {
      "respond with ActionPerformed message" in {
        val myProbe = TestProbe()
        val mockStockActor = TestMocks.getStockActor(MockStockRepository, myProbe)
        val basketActor = getBasketActor("Basket1", mockStockActor)

        basketActor ! Perform(Get, None)

        expectMsg(Content(Seq.empty))

        mockStockActor ! StopActor
        basketActor ! Stop
      }
    }

    "receives Add message for a product that exists in stock" should {
      "respond with ActionPerformed message" in {
        val myProbe = TestProbe()
        val mockStockActor = TestMocks.getStockActor(MockStockRepository, myProbe)
        val basketActor = getBasketActor("Basket2", mockStockActor)

        basketActor ! Perform(Add, Some(BasketProductEntry(1, 2)))

        myProbe.expectMsgAllOf(Get, AddOrReplaceExistingStock(1, 8))
        expectMsg(ActionPerformed(1))

        mockStockActor ! StopActor
        basketActor ! Stop
      }
    }

    "receives Add message for a product that already exists in basket" should {
      "respond with Conflict message" in {
        val myProbe = TestProbe()
        val mockStockActor = TestMocks.getStockActor(MockStockRepository, myProbe)
        val basketActor = getBasketActor("Basket3", mockStockActor)

        basketActor ! Perform(Add, Some(BasketProductEntry(1, 2)))

        myProbe.expectMsgAllOf(Get, AddOrReplaceExistingStock(1, 8))
        expectMsg(ActionPerformed(1))
        basketActor ! Perform(Add, Some(BasketProductEntry(1, 2)))
        expectMsg(Conflict(1))

        mockStockActor ! StopActor
        basketActor ! Stop
      }
    }

    "receives Update message for a product that already exists in basket" should {
      "respond with ActionPerformed message" in {
        val myProbe = TestProbe()
        val mockStockActor = TestMocks.getStockActor(MockStockRepository, myProbe)
        val basketActor = getBasketActor("Basket4", mockStockActor)

        basketActor ! Perform(Add, Some(BasketProductEntry(1, 2)))

        myProbe.expectMsgAllOf(Get, AddOrReplaceExistingStock(1, 8))
        expectMsg(ActionPerformed(1))

        basketActor ! Perform(Update, Some(BasketProductEntry(1, 6)))

        myProbe.expectMsgAllOf(Get, AddOrReplaceExistingStock(1, 6))
        expectMsg(ActionPerformed(1))

        mockStockActor ! StopActor
        basketActor ! Stop
      }
    }


    "receives Add message for a product that no longer exists in stock" should {
      "respond with ProductNotFound message" in {
        val myProbe = TestProbe()
        val mockStockActor = TestMocks.getStockActor(MockStockRepository, myProbe)
        val basketActor = getBasketActor("Basket5", mockStockActor)

        basketActor ! Perform(Add, Some(BasketProductEntry(99, 2)))

        myProbe.expectMsg(Get)
        expectMsg(ProductNotFound(99))

        mockStockActor ! StopActor
        basketActor ! Stop
      }
    }

    "receives Add message for a product that has insufficient stock" should {
      "respond with OutOfStock message" in {
        val myProbe = TestProbe()
        val mockStockActor = TestMocks.getStockActor(MockStockRepository, myProbe)
        val basketActor = getBasketActor(mockStockActor)

        basketActor ! Perform(Add, Some(BasketProductEntry(2, 200)))
        expectMsg(OutOfStock(2, 20))

        mockStockActor ! StopActor
        basketActor ! Stop
      }
    }

  }

  def getBasketActor(name: String, mockStockActor: ActorRef): ActorRef = {
    system.actorOf(BasketActor.props(mockStockActor), name)
  }

  override def afterAll: Unit = {
    TestKit.shutdownActorSystem(system)
  }
}