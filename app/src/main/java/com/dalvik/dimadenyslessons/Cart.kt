package com.dalvik.dimadenyslessons

class Cart(private val discountCalculator: DiscountCalculator) {

    private val price = 55

    fun getTotalPrice(): Int {
        return price - discountCalculator.getDiscount()
    }
}
