package com.lursachi.service

import akka.actor.{Actor, ActorRef, Props}
import com.lursachi.domain.BasketProductEntry
import com.lursachi.repository.{InMemoryProductRepository, ProductRepository}
import com.lursachi.service.Basket._
import com.lursachi.service.ServiceProtocol.{Add, Delete, Get}
import com.lursachi.service.Stock._
import com.lursachi.util.Functions._

import scala.concurrent.ExecutionContext

class BasketActor(stock: ActorRef, productRepo: ProductRepository = InMemoryProductRepository) extends Actor with State {

  override def receive: Receive = handleRequests(Map.empty)

  def handleRequests(basketProducts: Map[Int, Int]): Receive = {
    case Perform(Get, basketEntry) =>
      handleGetBasketContent(basketProducts)
    case Perform(Delete, basketEntry) =>
      handleDelete(basketProducts, basketEntry.get)
    case Perform(Add, entry) if entryAlreadyExists(entry, basketProducts) =>
      handleConflict(entry.get)
    case Perform(action, product) =>
      handleAddOrUpdate(basketProducts, product.get)
  }

  def handleGetBasketContent(basketProducts: Map[Int, Int]) = {
    implicit val repo = productRepo
    val products = basketProducts.toSeq.collect {
      toBasketProduct
    }
    sender() ! Content(products)
  }

  def handleDelete(basketProducts: Map[Int, Int], entry: BasketProductEntry) = {
    basketProducts.get(entry.productId) match {
      case None => sender ! ProductNotFound(entry.productId)
      case Some(qty) =>
        context.become(
          waitingForStockUpdate(entry, sender) {
            (key, _) => removeEntry(basketProducts, key)
          })

        stock ! AddToExistingStock(entry.productId, qty)
    }
  }

  def entryAlreadyExists(product: Option[BasketProductEntry], basketProducts: Map[Int, Int]) =
    basketProducts.contains(product.get.productId)

  def handleConflict(entry: BasketProductEntry) = sender() ! Conflict(entry.productId)

  def handleAddOrUpdate(basketProducts: Map[Int, Int], product: BasketProductEntry): Unit = {
    if (product.quantity > 0) {
      context.become(waitingForAvailableStock(product, basketProducts, sender()))
      stock ! Get
    }
    else {
      sender() ! InvalidAmount
    }
  }

  def waitingForAvailableStock(entry: BasketProductEntry, currentBasketProducts: Map[Int, Int], origin: ActorRef): Receive = {
    case AvailableStock(productsStock) =>
      def currentlyInBasket = currentBasketProducts.getOrElse(entry.productId, 0)

      def availableStock = productsStock.get(entry.productId).
        map(stock => stock + currentlyInBasket).
        filter(availableStock => availableStock >= entry.quantity)

      productsStock.get(entry.productId) match {
        case None =>
          context.become(handleRequests(currentBasketProducts))
          origin ! ProductNotFound(entry.productId)
        case Some(existingQty) if availableStock.isEmpty =>
          context.become(handleRequests(currentBasketProducts))
          origin ! Basket.OutOfStock(entry.productId, existingQty)
        case Some(existingQty) =>
          context.become(
            waitingForStockUpdate(entry, origin) { (productId, qty) =>
              addOrReplaceExisting(currentBasketProducts, productId, entry.quantity)
            }
          )
          stock ! AddOrReplaceExistingStock(entry.productId, existingQty + currentlyInBasket - entry.quantity)
      }

    case any =>
      context.parent forward any
  }

  def waitingForStockUpdate(product: BasketProductEntry, origin: ActorRef)(updateStateFor: (Int, Int) => Map[Int, Int]): Receive = {
    case StockUpdated =>
      context.become(handleRequests(updateStateFor(product.productId, product.quantity)))
      origin ! Basket.ActionPerformed(product.productId)
    case any =>
      context.parent forward any
  }
}

object BasketActor {
  def props(stock: ActorRef)(implicit ec: ExecutionContext) = Props(new BasketActor(stock))
}

