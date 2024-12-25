package com.dalvik.dimadenyslessons

fun String.normalizePhoneNumber(): String {
    return this
        .replace("^\\+".toRegex(), "") // Remove leading '+' if it exists
        .replace("\\s+".toRegex(), "") // Remove all spaces
        .replace("\\D".toRegex(), "") // Remove all non-digit characters
}