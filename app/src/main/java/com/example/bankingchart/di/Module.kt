package com.example.bankingchart.di

import com.example.bankingchart.MainViewModel
import com.example.bankingchart.Repository
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
 viewModel { MainViewModel(get()) }
  factory { Repository(get()) }
}