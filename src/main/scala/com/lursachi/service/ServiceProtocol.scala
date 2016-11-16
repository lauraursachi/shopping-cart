package com.lursachi.service


object ServiceProtocol {

  sealed trait Action

  object Get extends Action

  object Add extends Action

  object Update extends Action

  object Delete extends Action


  sealed trait ServiceResponse

  case class ActionPerformed[T](action: Action, productId: T) extends ServiceResponse

  case class InsufficientStock(productId: Int, qty: Int) extends ServiceResponse

  case class ProductNotFound(id: Int) extends ServiceResponse

  case class InternalError(msg: String) extends ServiceResponse

  object InvalidAmount extends ServiceResponse

  case class Conflict(productId: Int) extends ServiceResponse

}