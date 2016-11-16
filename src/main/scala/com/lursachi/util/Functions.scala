package com.lursachi.util

import com.lursachi.domain.BasketProduct
import com.lursachi.repository.ProductRepository


object Functions {

  def toBasketProduct(implicit repo: ProductRepository) = new PartialFunction[(Int, Int), BasketProduct] {
    def apply(basketEntry: (Int, Int)) = basketEntry match {
      case (id, qty) => BasketProduct(repo.find(id).get, qty)
    }

    def isDefinedAt(basketEntry: (Int, Int)) = repo.find(basketEntry._1) match {
      case Some(product) => true
      case None => false
    }
  }
}
