package com.lursachi.service

import akka.pattern.ask
import com.lursachi.domain.BasketProductEntry
import com.lursachi.service.Basket._
import com.lursachi.service.ServiceProtocol.{ActionPerformed, Conflict, InternalError, InvalidAmount, ProductNotFound, _}
import com.lursachi.util.Constants._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait BasketService extends ActorSystemAware {

  def perform(action: Action, basketEntryOption: Option[BasketProductEntry]): Future[ServiceResponse] = {
    val basketActor = getBasketActor()
    (basketActor ? Perform(action, basketEntryOption))
      .flatMap {
        case Basket.Content(products) => Future.successful(ActionPerformed(action, products))
        case Basket.ActionPerformed(id) => Future.successful(ActionPerformed(action, id))
        case Basket.InvalidAmount => Future.successful(InvalidAmount)
        case Basket.OutOfStock(id, qty) => Future.successful(InsufficientStock(id, qty))
        case Basket.ProductNotFound(id) => Future.successful(ProductNotFound(id))
        case Basket.Conflict(id) => Future.successful(Conflict(id))
        case _ => Future.successful(InternalError(s"Unable to perform basket $action request"))
      }
  }
}


