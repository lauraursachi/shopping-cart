package com.lursachi.http

import akka.http.scaladsl.model.{StatusCodes, _}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.lursachi.service.TestMocks._
import org.scalatest.{Matchers, WordSpec}
import spray.json._


class BasketServiceRouteSpec extends WordSpec
  with ScalatestRouteTest
  with Matchers {

  "a route" when {

    "receives Get request for an empty basket" should {
      "return 200(ok) http response" in {
        Get("/basket") ~> RouteForEmptyBasket.basketRoute ~> check {

          val res = responseAs[HttpResponse]
          res.status shouldEqual StatusCodes.OK
        }
      }
    }

    "receives Get request for a basket with one product" should {
      "return 200(ok) http response" in {
        Get("/basket") ~> RouteForOneProductInBasket.basketRoute ~> check {

          val res = responseAs[HttpResponse]
          res.status shouldEqual StatusCodes.OK
        }
      }
    }

    "receives Post request for an invalid amount (<=0) " should {
      "return 403(Forbidden) http response" in {

        val requestEntity = HttpEntity(MediaTypes.`application/json`,
          JsObject(("productId", JsNumber(1)), ("quantity", JsNumber(0))).toString())

        Post("/basket", requestEntity) ~> TestedRoute.basketRoute ~> check {

          val res = responseAs[HttpResponse]
          res.status shouldEqual StatusCodes.Forbidden
        }
      }
    }

    "receives Post request for a product that no longer exists" should {
      "return 404(Not found) http response" in {

        val requestEntity = HttpEntity(MediaTypes.`application/json`,
          JsObject(("productId", JsNumber(99)), ("quantity", JsNumber(2))).toString())

        Post("/basket", requestEntity) ~> TestedRoute.basketRoute ~> check {

          val res = responseAs[HttpResponse]
          res.status shouldEqual StatusCodes.NotFound
          res.entity shouldEqual HttpEntity("99")
        }
      }
    }

    "receives Post request for a product with insufficient stock" should {
      "return 422(Unprocessable Entity) http response" in {

        val requestEntity = HttpEntity(MediaTypes.`application/json`,
          JsObject(("productId", JsNumber(1)), ("quantity", JsNumber(200))).toString())

        Post("/basket", requestEntity) ~> TestedRoute.basketRoute ~> check {

          val res = responseAs[HttpResponse]
          res.status shouldEqual StatusCodes.UnprocessableEntity
        }
      }
    }

    "receives Post request for a product with sufficient stock" should {
      "return 201(Created) http response and the product recource location" in {

        val requestEntity = HttpEntity(MediaTypes.`application/json`,
          JsObject(("productId", JsNumber(1)), ("quantity", JsNumber(5))).toString())

        Post("/basket", requestEntity) ~> TestedRoute.basketRoute ~> check {

          val res = responseAs[HttpResponse]
          res.status shouldEqual StatusCodes.Created
          res.entity shouldEqual HttpEntity("/basket/1")
        }
      }
    }

    "receives Put request for existing product" should {
      "return 204(No Content) http response" in {
        Put("/basket/1", HttpEntity("6")) ~> RouteForOneProductInBasket.basketRoute ~> check {

          val res = responseAs[HttpResponse]
          res.status shouldEqual StatusCodes.NoContent
        }
      }
    }

    "receives Delete request for existing product" should {
      "return 200(Ok) http response" in {
        Delete("/basket/1") ~> RouteForExistingProductInBasket.basketRoute ~> check {

          val res = responseAs[HttpResponse]
          res.status shouldEqual StatusCodes.OK
          res.entity shouldEqual HttpEntity("Product 1 was successfully removed from basket")
        }
      }
    }

    "receives Delete request for non-existing product" should {
      "return 404(Not Found) http response" in {
        Delete("/basket/1") ~> RouteForNonExistingProductInBasket.basketRoute ~> check {

          val res = responseAs[HttpResponse]
          res.status shouldEqual StatusCodes.NotFound
        }
      }
    }
  }

}



