package com.example.foodrunner.activity

import android.app.Activity
import android.app.AlertDialog
import android.app.PendingIntent.getActivity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.audiofx.BassBoost
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.provider.Settings.ACTION_WIFI_SETTINGS
import android.provider.Settings.ACTION_WIRELESS_SETTINGS
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodrunner.R
import com.example.foodrunner.util.ConnectionManager
import org.json.JSONObject


class MainActivity : AppCompatActivity() {

    lateinit var login: Button
    lateinit var forgotpassword: TextView
    lateinit var signup: TextView
    lateinit var sharedPreferences: SharedPreferences
    lateinit var connectionManager: ConnectionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val mobilenumber: EditText = findViewById(R.id.MobileNumber)
        val txtpassword: EditText = findViewById(R.id.txtPassword)
        forgotpassword = findViewById(R.id.ForgotPassword)
        signup = findViewById(R.id.SignIn)
        login = findViewById(R.id.btnLogin)
        sharedPreferences=getSharedPreferences("Preferences",Context.MODE_PRIVATE)
        val queue = Volley.newRequestQueue(this@MainActivity)
        val url = "http://13.235.250.119/v2/login/fetch_result/"


        login.setOnClickListener {

            val jsonParams = JSONObject()
            jsonParams.put("mobile_number", mobilenumber.text.toString())
            jsonParams.put("password", txtpassword.text.toString())
            if (ConnectionManager().checkConnectivity(this@MainActivity)) {
                val jsonObjectRequest = object : JsonObjectRequest(Method.POST, url, jsonParams,
                    Response.Listener {
                        val data1 = it.getJSONObject("data")
                        val success = data1.getBoolean("success")

                        if (success) {
                            val data = data1.getJSONObject("data")

                            val user_id = data.getString("user_id")
                            val name = data.getString("name")
                            val email = data.getString("email")
                            val mob_no = data.getString("mobile_number")
                            val address = data.getString("address")
                            sharedPreferences.edit().putBoolean("isLoggedIn", true).apply()
                            sharedPreferences.edit().putString("user_id", user_id).apply()
                            sharedPreferences.edit().putString("Name", name).apply()
                            sharedPreferences.edit().putString("MobNo", mob_no).apply()
                            sharedPreferences.edit().putString("Email", email).apply()
                            sharedPreferences.edit().putString("Address", address).apply()

                            val intent = Intent(this@MainActivity, MainActivity2::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(
                                this@MainActivity,
                                data1.getString("errorMessage"),
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
                val dialog = AlertDialog.Builder(this@MainActivity)
                dialog.setTitle("Error")
                dialog.setMessage("Internet Connection not Found")

                dialog.setPositiveButton("Open Settings") { text, listener ->
                    val settingsIntent = Intent(Settings.ACTION_WIFI_SETTINGS)
                    startActivity(settingsIntent)
                    finish()
                }
                dialog.setNegativeButton("Exit") { text, listener ->
                    ActivityCompat.finishAffinity(this@MainActivity)
                }
                dialog.create()
                dialog.show()
            }
            }


        signup.setOnClickListener {
            val intent = Intent(this@MainActivity,RegisterScreen::class.java)
            startActivity(intent)
        }
        forgotpassword.setOnClickListener{
            val intent=Intent(this@MainActivity,ForgotPassword::class.java)
            startActivity(intent)
        }


    }






}
