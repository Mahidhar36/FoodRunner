package com.example.foodrunner.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.foodrunner.R

class SplashScreen : AppCompatActivity() {
    lateinit var  sharedPreferences:SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
         sharedPreferences=getSharedPreferences("Preferences", Context.MODE_PRIVATE)
        if(sharedPreferences.getBoolean("isLoggedIn",false)) {
            Handler().postDelayed({
                val intent = Intent(this@SplashScreen, MainActivity2::class.java)
                startActivity(intent)
                finish()

            }, 2000)
        }
        else{
            Handler().postDelayed({
                val intent = Intent(this@SplashScreen, MainActivity::class.java)
                startActivity(intent)
                finish()

            }, 2000)
        }

    }
}