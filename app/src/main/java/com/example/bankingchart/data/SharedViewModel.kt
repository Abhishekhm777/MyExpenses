package com.example.bankingchart.data

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bankingchart.model.SmsModel

class SharedViewModel(private val repository: Repository) : ViewModel() {
    var allsms: MutableLiveData<List<SmsModel>> = MutableLiveData()

    init {
        allsms = repository.allsms
    }
    fun getSms(): MutableLiveData<List<SmsModel>> = allsms

    suspend fun refresh() {
        repository.getAllSms()
    }


}