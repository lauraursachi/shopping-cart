package com.lursachi.util

import akka.util.Timeout
import scala.concurrent.duration._

object Constants {

    val actorSystemName = "basket-system"

    val duration = 5 seconds
    implicit val timeout = Timeout(duration)

}
