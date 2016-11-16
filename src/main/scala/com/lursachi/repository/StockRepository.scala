package com.lursachi.repository

import com.lursachi.repository.Const._

trait StockRepository {
  def getAll: Map[Int, Int]
}

object InMemoryStockRepository extends StockRepository {
  override def getAll: Map[Int, Int] = Map(Product_1_Id -> 10, Product_2_Id -> 20)
}
