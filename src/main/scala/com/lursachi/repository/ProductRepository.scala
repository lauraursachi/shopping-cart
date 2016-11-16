package com.lursachi.repository

import com.lursachi.domain.Product
import com.lursachi.repository.Const._

trait ProductRepository {

  def find(id: Int): Option[Product]

}

object InMemoryProductRepository extends ProductRepository {
  val products: Map[Int, Product] = Map(
    Product_1_Id -> Product(Product_1_Id, "Apples", "Apples", BigDecimal("3")),
    Product_2_Id -> Product(Product_2_Id, "Book", "Books", BigDecimal("9.99")))

  override def find(id: Int) = products.get(id)


}


