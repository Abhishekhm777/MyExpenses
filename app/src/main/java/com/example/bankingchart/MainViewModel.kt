package com.example.bankingchart

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel(private val repository: Repository) :ViewModel() {

    var   allsms = repository.allsms
    /* private var   allsms :MutableLiveData<List<SmsModel>> = MutableLiveData()

      fun getSms():MutableLiveData<List<SmsModel>>{
            if(allsms.value?.size==0){
                  allsms = repository.allsms
                  return  allsms
            }else  return allsms

      }*/
}