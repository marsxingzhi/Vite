package com.mars.infra.vite

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mars.infra.login.LoginService

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()
        val loginService = LoginService()
        loginService.login()
    }
}