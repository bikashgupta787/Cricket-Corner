package com.example.ecommerceapp.Data.Order

import com.example.ecommerceapp.Data.Address
import com.example.ecommerceapp.Data.CartProduct

data class Order(
    val orderStatus: String,
    val totalPrice: Float,
    val products: List<CartProduct>,
    val address: Address
)
