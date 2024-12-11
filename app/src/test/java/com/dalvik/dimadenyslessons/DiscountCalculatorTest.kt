package com.dalvik.dimadenyslessons

import junit.framework.TestCase.assertEquals
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever


class DiscountCalculatorTest {

    private val calendar = mock<Calendar>()

    @Test
    fun `should return discount for weekend`() {
        whenever(calendar.isWeekend()).thenReturn(true)
        val calculator = DiscountCalculator(calendar)

        assertEquals(5, calculator.getDiscount())
    }

    @Test
    fun `should return discount for weekday`() {
        whenever(calendar.isWeekend()).thenReturn(false)
        val calculator = DiscountCalculator(calendar)

        assertEquals(100, calculator.getDiscount())
    }
}
