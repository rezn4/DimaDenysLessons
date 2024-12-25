package com.dalvik.dimadenyslessons.dec262024.repository

import com.dalvik.dimadenyslessons.dec262024.models.Contact

interface ContactsRepository {
    suspend fun loadContacts(): List<Contact>
}