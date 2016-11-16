package com.lursachi.http

import akka.http.scaladsl.model.{HttpEntity, _}
import com.lursachi.domain.{BasketProduct, Product}
import com.lursachi.service.ServiceProtocol._
import org.scalatest.{Matchers, WordSpec}


class HttpResponseConverterSpec extends WordSpec
  with Matchers {


  "HttpResponseConverter" when {

    "receives ActionPerformed for Get message for an non-empty basket" should {
      "convert it to 200" in {
        val httpResponse = HttpResponseConverter.toHttpResponse(ActionPerformed(Get,
          Seq(BasketProduct(Product(1, "Apple", "apples", BigDecimal("9.99")), 5))))


        httpResponse.status shouldEqual StatusCodes.OK
        httpResponse.entity shouldEqual HttpEntity(ContentType(MediaTypes.`application/json`),
          s"""[{"product":{"id":1,"name":"Apple","description":"apples","price":9.99},"quantity":5}]""")
      }
    }

    "receives ActionPerformed for Add message for a product that exists in stock" should {
      "convert it to Created status code" in {
        val httpResponse = HttpResponseConverter.toHttpResponse(ActionPerformed(Add, 1))


        httpResponse.status shouldEqual StatusCodes.Created
        httpResponse.entity shouldEqual HttpEntity("/basket/1")
      }
    }

    "receives ActionPerformed for Update message" should {
      "convert it to NoContent status code" in {
        val httpResponse = HttpResponseConverter.toHttpResponse(ActionPerformed(Update, 1))


        httpResponse.status shouldEqual StatusCodes.NoContent
      }
    }

    "receives ActionPerformed for Delete message" should {
      "convert it to OK status code" in {
        val httpResponse = HttpResponseConverter.toHttpResponse(ActionPerformed(Delete, 1))


        httpResponse.status shouldEqual StatusCodes.OK
        httpResponse.entity shouldEqual HttpEntity("Product 1 was successfully removed from basket")
      }
    }

    "receives InsufficientStock" should {
      "convert it to UnprocessableEntity status code" in {
        val httpResponse = HttpResponseConverter.toHttpResponse(InsufficientStock(1, 19))


        httpResponse.status shouldEqual StatusCodes.UnprocessableEntity
        httpResponse.entity shouldEqual HttpEntity("Insufficient stock 19 for product 1")
      }
    }

    "receives InvalidAmount" should {
      "convert it to Forbidden status code" in {
        val httpResponse = HttpResponseConverter.toHttpResponse(InvalidAmount)


        httpResponse.status shouldEqual StatusCodes.Forbidden
        httpResponse.entity shouldEqual HttpEntity("Invalid amount")
      }
    }

    "receives Conflict" should {
      "convert it to Conflict status code" in {
        val httpResponse = HttpResponseConverter.toHttpResponse(Conflict(1))


        httpResponse.status shouldEqual StatusCodes.Conflict
        httpResponse.entity shouldEqual HttpEntity("Product 1 already in the basket")
      }
    }
  }
}
