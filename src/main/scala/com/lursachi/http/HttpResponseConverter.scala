package com.lursachi.http

import akka.http.scaladsl.model._
import com.lursachi.domain.BasketProduct
import com.lursachi.service.ServiceProtocol._
import spray.json._

object HttpResponseConverter extends BasketSerializers {

  def toHttpResponse(serviceResponse: ServiceResponse): HttpResponse =
    serviceResponse match {
      case ActionPerformed(Get, basketContent) =>
        HttpResponse(
          status = StatusCodes.OK,
          entity = HttpEntity(ContentType(MediaTypes.`application/json`),s"""${basketContent.asInstanceOf[Seq[BasketProduct]].toJson}"""))
      case ActionPerformed(Add, id) =>
        HttpResponse(
          status = StatusCodes.Created,
          entity = HttpEntity(s"/basket/$id"))
      case ActionPerformed(Update, id) =>
        HttpResponse(
          status = StatusCodes.NoContent)
      case ActionPerformed(Delete, id) =>
        HttpResponse(
          status = StatusCodes.OK,
          entity = HttpEntity(s"Product $id was successfully removed from basket"))
      case InsufficientStock(id, qty) =>
        HttpResponse(
          status = StatusCodes.UnprocessableEntity,
          entity = HttpEntity(s"Insufficient stock $qty for product $id"))
      case InvalidAmount =>
        HttpResponse(
          status = StatusCodes.Forbidden,
          entity = HttpEntity("Invalid amount"))
      case ProductNotFound(id) =>
        HttpResponse(
          status = StatusCodes.NotFound,
          entity = HttpEntity(id.toString))
      case Conflict(id) =>
        HttpResponse(
          status = StatusCodes.Conflict,
          entity = HttpEntity(s"Product $id already in the basket"))
      case InternalError(msg) =>
        HttpResponse(
          status = StatusCodes.InternalServerError,
          entity = HttpEntity(msg))
    }

}
