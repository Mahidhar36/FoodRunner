package com.example.foodrunner.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat

import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodrunner.R
import com.example.foodrunner.util.ConnectionManager
import org.json.JSONException
import org.json.JSONObject

class RegisterScreen : AppCompatActivity() {

    lateinit var toolbar: Toolbar
    lateinit var etName: EditText
    lateinit var etEmail: EditText
    lateinit var etMobileNumber: EditText
    lateinit var etAddress: EditText
    lateinit var etPassword: EditText
    lateinit var etConfirmPassword: EditText
    lateinit var btnRegister: Button

    lateinit var sharedPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences("Preferences", Context.MODE_PRIVATE)
        setContentView(R.layout.activity_registration_screen)
        initializeView()

    }

    fun startHomeScreen() {
        val intent = Intent(this@RegisterScreen, MainActivity2::class.java)
        startActivity(intent)
        finish()
    }

    private fun validateRegisterFields() = (etName.text.toString().length >= 3
            && etEmail.text.toString().isNotEmpty()
            && etMobileNumber.text.toString().length == 10
            && etAddress.text.toString().isNotEmpty() &&
            etPassword.text.toString().length >= 6
            && etConfirmPassword.text.toString().length >= 6
            && (etPassword.text.toString() == etConfirmPassword.text.toString()))

    fun validateAndRegister() {

        val queue = Volley.newRequestQueue(this@RegisterScreen)
        val url = "http://13.235.250.119/v2/register/fetch_result"
        Toast.makeText(this@RegisterScreen, "${etName.text.toString()}", Toast.LENGTH_SHORT).show()
        val jsonParams = JSONObject()

        jsonParams.put("name", etName.text.toString())
        jsonParams.put("mobile_number", etMobileNumber.text.toString())
        jsonParams.put("password", etPassword.text.toString())
        jsonParams.put("address", etAddress.text.toString())
        jsonParams.put("email", etEmail.text.toString())
        if (ConnectionManager().checkConnectivity(this@RegisterScreen)) {


            val jsonRequest = object : JsonObjectRequest(
                Request.Method.POST, url, jsonParams, Response.Listener {
                    val mainData = it.getJSONObject("data")

                    try {
                        val success = mainData.getBoolean("success")
                        println("success = $success")

                        //fix error during registration
                        if (success) {

                            println("Registration Successful!!")
                            val data = mainData.getJSONObject("data")

                            val user_id = data.getString("user_id")
                            val name = data.getString("name")
                            val email = data.getString("email")
                            val mob_no = data.getString("mobile_number")
                            val address = data.getString("address")

                            //saving preferences..

                            sharedPreferences.edit().putBoolean("isLoggedIn", true).apply()
                            sharedPreferences.edit().putString("user_id", user_id).apply()
                            sharedPreferences.edit().putString("Name", name).apply()
                            sharedPreferences.edit().putString("MobNo", mob_no).apply()
                            sharedPreferences.edit().putString("Email", email).apply()
                            sharedPreferences.edit().putString("Address", address).apply()

                            //starting homescreen
                            startHomeScreen()

                        } else {
                            Toast.makeText(
                                this@RegisterScreen,
                                "Registration Failed",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }

                    } catch (e: JSONException) {
                        Toast.makeText(
                            this@RegisterScreen,
                            "Some unexpected error occurred!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                },
                Response.ErrorListener {
                    Toast.makeText(
                        this@RegisterScreen,
                        "Volley error occurred!!",
                        Toast.LENGTH_SHORT
                    ).show()
                }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-type"] = "application/json"
                    headers["token"] = "c6fb515078cf22"
                    return headers
                }
            }

            queue.add(jsonRequest)

            //for internet connectivity part

        }

        else{
            val dialog = android.app.AlertDialog.Builder(this@RegisterScreen)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection not Found")

            dialog.setPositiveButton("Open Settings") { text, listener ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                finish()
            }
            dialog.setNegativeButton("Exit") { text, listener ->
                ActivityCompat.finishAffinity(this@RegisterScreen)
            }
            dialog.create()
            dialog.show()

        }
    }

    private fun initializeView() {
        toolbar = findViewById(R.id.toolbar1)
        toolbar.title = "Register Yourself"
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        etName = findViewById(R.id.etName)
        etEmail = findViewById(R.id.etEmail)
        etMobileNumber = findViewById(R.id.etMobileNumber)
        etAddress = findViewById(R.id.etAddress)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        btnRegister = findViewById(R.id.btnRegister)

        btnRegister.setOnClickListener {

            if (validateRegisterFields()) {

                //function called for validating registration
                validateAndRegister()

            } else {
                Toast.makeText(
                    this@RegisterScreen,
                    "Incomplete credentials",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }


    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

}