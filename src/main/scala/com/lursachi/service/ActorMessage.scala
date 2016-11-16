package com.lursachi.service

import com.lursachi.domain.{BasketProduct, BasketProductEntry}
import com.lursachi.service.ServiceProtocol.Action


sealed trait ActorMessage

object Basket {

  case class Perform(action: Action, basketEntryOption: Option[BasketProductEntry]) extends ActorMessage

  case class Content(products: Seq[BasketProduct]) extends ActorMessage

  case class ProductNotFound(id: Int) extends ActorMessage

  case class ActionPerformed(productId: Int)

  object InvalidAmount extends ActorMessage

  case class OutOfStock(id: Int, qty: Int) extends ActorMessage

  case class Conflict(id: Int) extends ActorMessage

}

object Stock {

  case class AddOrReplaceExistingStock(id: Int, qty: Int) extends ActorMessage

  case class AddToExistingStock(id: Int, qty: Int) extends ActorMessage

  case class AvailableStock(stock: Map[Int, Int]) extends ActorMessage

  case object StockUpdated extends ActorMessage

  case class OutOfStock(id: Int) extends ActorMessage

}



