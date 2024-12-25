package com.dalvik.dimadenyslessons.dec262024.repository

import android.content.Context
import android.provider.ContactsContract
import com.dalvik.dimadenyslessons.dec262024.models.Contact
import com.dalvik.dimadenyslessons.normalizePhoneNumber
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import javax.inject.Inject

class ContactsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : ContactsRepository {
    override suspend fun loadContacts(): List<Contact> {
        val contactsList = mutableListOf<Contact>()

        val cursor = context.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            arrayOf(
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
            ),
            null,
            null,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        )

        delay(3000)

        cursor?.use {
            val nameIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val numberIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

            while (it.moveToNext()) {
                val name = it.getString(nameIndex)
                val phoneNumber = it.getString(numberIndex)

                contactsList.add(Contact(name, phoneNumber))
            }
        }

        return removeDuplicates(contactsList)
    }

    private fun removeDuplicates(contacts: List<Contact>): List<Contact> {
        return contacts
            .groupBy { it.phoneNumber.normalizePhoneNumber() }
            .map {
                val name = it.value.first().name
                val phone = it.key

                Contact(name, phone)
            }
    }
}