package com.example.ecommerceapp.Helper

fun Float?.getProductPrice(price:Float):Float{
    if (this == null)
        return price
    val remainingPricePercentage = 1f - (this / 100)
    val priceAfterOffer = remainingPricePercentage * price

    return priceAfterOffer
}