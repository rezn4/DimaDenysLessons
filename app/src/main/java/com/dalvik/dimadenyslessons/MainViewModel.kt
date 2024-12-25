package com.dalvik.dimadenyslessons

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dalvik.dimadenyslessons.dec262024.repository.ContactsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val contactsRepository: ContactsRepository
): ViewModel() {
    fun loadContacts(){
        viewModelScope.launch {
            val contacts = contactsRepository.loadContacts()

            contacts.forEach {
                Log.d("contacts", it.toString())
            }
        }
    }
}