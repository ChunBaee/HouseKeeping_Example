package com.solie.housekeeping_example.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HouseKeepViewModel : ViewModel(), FirebaseData {
   private val _newMonth = MutableLiveData<String>(month.format(time).toString())
    val newMonth : LiveData<String> get() = _newMonth


    fun updateNewMonth(new : String) {
        _newMonth.value = new
    }

}