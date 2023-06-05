package com.example.foodrunner.activity

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodrunner.R
import com.example.foodrunner.util.ConnectionManager
import org.json.JSONObject

class ResetPassword : AppCompatActivity() {
    lateinit var toolbar:Toolbar
    lateinit var otp:EditText
    lateinit var newpassword:EditText
    lateinit var confirmpassword:EditText
    lateinit var Submit:Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)
        toolbar=findViewById(R.id.toolbar2)
        otp=findViewById(R.id.otp)
        newpassword=findViewById(R.id.NewPassword)
        confirmpassword=findViewById(R.id.ConfirmPassword)
        Submit=findViewById(R.id.Submit)
        setUpToolBar()
        val text="7981991214"

        val queue=Volley.newRequestQueue(this@ResetPassword)
        val url="http://13.235.250.119/v2/reset_password/fetch_result"
        Submit.setOnClickListener {
            val jsonParams = JSONObject()
            jsonParams.put("mobile_number", text)
            jsonParams.put("password", confirmpassword.text.toString())
            jsonParams.put("otp", otp.text.toString())
            if (ConnectionManager().checkConnectivity(this@ResetPassword)) {
                val jsonObjectRequest =
                    object : JsonObjectRequest(Request.Method.POST, url, jsonParams,
                        Response.Listener {
                            val data = it.getJSONObject("data")
                            val success = data.getBoolean("success")
                            if (success) {

                                val intent = Intent(this@ResetPassword, MainActivity2::class.java)
                                startActivity(intent)
                            } else {
                                Toast.makeText(
                                    this@ResetPassword,
                                    data.getString("successMessage"),
                                    Toast.LENGTH_SHORT
                                ).show()
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
            else{
                val dialog = AlertDialog.Builder(this@ResetPassword)
                dialog.setTitle("Error")
                dialog.setMessage("Internet Connection not Found")

                dialog.setPositiveButton("Open Settings") { text, listener ->
                    val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                    startActivity(settingsIntent)
                    finish()
                }
                dialog.setNegativeButton("Exit") { text, listener ->
                    ActivityCompat.finishAffinity(this@ResetPassword)
                }
                dialog.create()
                dialog.show()

            }
        }

    }
fun setUpToolBar(){
    setSupportActionBar(toolbar)
    supportActionBar?.title="Reset Your Password"
    supportActionBar?.setHomeButtonEnabled(true)
    supportActionBar?.setDisplayHomeAsUpEnabled(true)}




}
