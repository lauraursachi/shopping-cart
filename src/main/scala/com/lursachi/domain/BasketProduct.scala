package com.lursachi.domain

case class BasketProduct(product: Product, quantity: Int)

case class BasketProductEntry(productId: Int, quantity: Int = 0)

case class Product(id: Int, name: String, description: String, price: BigDecimal)
