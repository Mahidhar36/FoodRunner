package com.example.foodrunner

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class drawerActivity : AppCompatActivity() {
    lateinit var username: TextView
    lateinit var userphonenumber: TextView
    lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drawer)
        username=findViewById(R.id.UserName)
        userphonenumber=findViewById(R.id.UserPhoneNumber)

        sharedPreferences=getSharedPreferences("Preferences", Context.MODE_PRIVATE)

        username.text=  sharedPreferences?.getString("Name", "DEFAULT_NAME")
       userphonenumber.text= sharedPreferences?.getString("MobNo", "DEFAULT_MOB")








    }
}