package com.lursachi.http

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.lursachi.domain.{BasketProduct, Product, BasketProductEntry}
import spray.json.DefaultJsonProtocol

trait BasketSerializers extends SprayJsonSupport with DefaultJsonProtocol {

  implicit val productFormat = jsonFormat4(Product)
  implicit val basketProductFormat = jsonFormat2(BasketProduct)
  implicit val productUpdateFormat = jsonFormat2(BasketProductEntry)

}

