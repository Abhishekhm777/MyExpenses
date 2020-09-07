package com.example.bankingchart.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.Navigation
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.bankingchart.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val controler  = Navigation.findNavController(
            this, R.id.nav_host
        )
        setupActionBarWithNavController(controler)
    }
}