package com.example.foodrunner.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodrunner.R
import org.json.JSONObject

class ForgotPassword : AppCompatActivity() {
    lateinit var mobile: EditText
    lateinit var email: EditText
    lateinit var next: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgor_password)
        mobile = findViewById(R.id.txtMobile)
        email = findViewById(R.id.txtEmail)
        next = findViewById(R.id.Next)


        val queue = Volley.newRequestQueue(this@ForgotPassword)
        val url = "http://13.235.250.119/v2/forgot_password/fetch_result"
        next.setOnClickListener {
            val jsonParams = JSONObject()
            jsonParams.put("mobile_number", mobile.text.toString())
            jsonParams.put("email", email.text.toString())
            val jsonObjectRequest = object : JsonObjectRequest(Request.Method.POST, url, jsonParams,
                Response.Listener {
                val data=it.getJSONObject("data")
                    val success=data.getBoolean("success")
                    if(success){
                        val intent=Intent(this@ForgotPassword,ResetPassword::class.java)

                        startActivity(intent)
                      finish()
                    }
                    else{
                        Toast.makeText(this@ForgotPassword, "some error occurred", Toast.LENGTH_SHORT).show()
                    }
                }, Response.ErrorListener { }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-type"] = "application/json"
                    headers["token"] = "c6fb515078cf22"
                    return headers
                }
            }

            queue.add(jsonObjectRequest)
        }

    }

}


