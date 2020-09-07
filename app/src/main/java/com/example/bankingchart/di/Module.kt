package com.example.bankingchart.di

import com.example.bankingchart.data.SharedViewModel
import com.example.bankingchart.data.Repository
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
 viewModel { SharedViewModel(get()) }
  single { Repository(get()) }
}