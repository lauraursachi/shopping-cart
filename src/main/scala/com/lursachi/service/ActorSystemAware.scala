package com.lursachi.service

import akka.actor.{ActorNotFound, ActorRef, ActorSystem, Props}
import com.lursachi.repository.{InMemoryStockRepository, StockRepository}
import com.lursachi.util.Constants._

import scala.concurrent.{Await, ExecutionContext}

trait ActorSystemAware {

  implicit def system: ActorSystem

  implicit def executionContext: ExecutionContext

  private def path(name: String) = s"akka://${system.name}/user/$name"

  def getBasketActor(stock: ActorRef = getStockActor()) = getActor("Basket", BasketActor.props(stock))

  def getStockActor(stockRepository: StockRepository = InMemoryStockRepository) = getActor("Stock", StockActor.props(stockRepository))

  private def getActor(name: String, props: Props) = {
    Await.result(getOrCreateActor(path(name), system.actorOf(props, name)), duration)
  }

  private def getOrCreateActor(path: String, default: => ActorRef) =
    system
      .actorSelection(path)
      .resolveOne(duration)
      .recover { case _: ActorNotFound => default }

}
