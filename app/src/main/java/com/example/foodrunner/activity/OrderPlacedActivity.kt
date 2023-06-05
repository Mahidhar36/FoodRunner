package com.example.foodrunner.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.foodrunner.R
import com.example.foodrunner.fragment.HomeFragment

class OrderPlacedActivity : AppCompatActivity() {
    lateinit var button: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_placed)


        button=findViewById(R.id.btnOkOrder)
button.setOnClickListener{
    val intent=Intent(this@OrderPlacedActivity, MainActivity2::class.java)
    startActivity(intent)
}

    }
}