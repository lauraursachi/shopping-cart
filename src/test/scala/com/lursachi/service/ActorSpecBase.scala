package com.lursachi.service

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfterAll, Suite}


class ActorSpecBase extends TestKit(ActorSystem("TestActorSystem"))
  with ActorSystemAware
  with ImplicitSender
  with Suite
  with BeforeAndAfterAll {

  override implicit def executionContext = system.dispatcher

  override def afterAll: Unit = {
    system.terminate()
  }

}
