package com.dalvik.dimadenyslessons

class DiscountCalculator(val calendar: Calendar) {

    fun getDiscount(): Int {
        return if (calendar.isWeekend()) {
            5
        } else {
            100
        }
    }
}

// third party SDK
class Calendar {

    fun isWeekend() = true

    fun isMonday() = false
}
