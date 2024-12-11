package com.dalvik.dimadenyslessons

import junit.framework.TestCase.assertEquals
import org.junit.Test

class CartTest {

    val cart = Cart(DiscountCalculator(Calendar()))

    @Test
    fun `should calculate total price`() {
        assertEquals(50, cart.getTotalPrice())
    }
}
