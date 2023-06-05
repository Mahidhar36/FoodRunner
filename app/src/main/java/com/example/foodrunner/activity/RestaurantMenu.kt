package com.example.foodrunner.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodrunner.R
import com.example.foodrunner.adapter.DBAsyncTask
import com.example.foodrunner.adapter.RestaurantMenuAdapter
import com.example.foodrunner.database.ItemDataBase
import com.example.foodrunner.database.ItemsEntity
import com.example.foodrunner.database.RestaurantEntity
import com.example.foodrunner.model.RestaurantMenu
import com.example.foodrunner.util.ConnectionManager

class RestaurantMenu : AppCompatActivity() {
    lateinit var resname: androidx.appcompat.widget.Toolbar
    lateinit var recycleritems: RecyclerView
    lateinit var recycleradapter: RestaurantMenuAdapter
    lateinit var progresslayout: RelativeLayout
    lateinit var progressBar: ProgressBar
    lateinit var layoutManager: RecyclerView.LayoutManager
    var ItemsName = arrayListOf<RestaurantMenu>()
    lateinit var proceedTOCart: Button
    var name: String? = null
    var restaurant_id: Int? = null
    var id: String? = null

    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant_menu)
        recycleritems = findViewById(R.id.RecyclerItems)
        resname = findViewById(R.id.Toolbar)
        layoutManager = LinearLayoutManager(this@RestaurantMenu)
        progresslayout = findViewById(R.id.ProgressLayout2)
        progressBar = findViewById(R.id.Progressbar1)
        progresslayout.visibility = View.VISIBLE
        progressBar.visibility = View.VISIBLE
        proceedTOCart = findViewById(R.id.ProceedToCart)
       fun onSupportNavigateUp(): Boolean {
            onBackPressed()
            return true
        }
        if (intent != null) {

            name = intent.getStringExtra("name")
            id = intent?.getStringExtra("id")
            restaurant_id = id?.toInt()
            setUpToolBar()
            val stack = Volley.newRequestQueue(this@RestaurantMenu)
            val url = "http://13.235.250.119/v2/restaurants/fetch_result/${restaurant_id}"
            if (ConnectionManager().checkConnectivity(this@RestaurantMenu)) {
                val jsonObjectRequest =
                    object : JsonObjectRequest(
                        Request.Method.GET, url, null, Response.Listener {
                            val data = it.getJSONObject("data")
                            val success = data.getBoolean("success")
                            if (success) {

                                progresslayout.visibility = View.GONE
                                val data1 = data.getJSONArray("data")

                                for (i in 0 until data1.length()) {
                                    val datarestaurantObject = data1.getJSONObject(i)
                                    val restaurantObject = RestaurantMenu(
                                        datarestaurantObject.getString("id"),
                                        datarestaurantObject.getString("name"),
                                        datarestaurantObject.getString("cost_for_one"),
                                        datarestaurantObject.getString("restaurant_id")


                                    )

                                    ItemsName.add(restaurantObject)
                                    recycleradapter =
                                        RestaurantMenuAdapter(this@RestaurantMenu, ItemsName)
                                    recycleritems.adapter = recycleradapter
                                    recycleritems.layoutManager = layoutManager
                                    recycleritems.setHasFixedSize(true)


                                }


                            } else {
                                Toast.makeText(
                                    this@RestaurantMenu,
                                    "some error occurred",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        },
                        Response.ErrorListener { }) {
                        override fun getHeaders(): MutableMap<String, String> {
                            val headers = HashMap<String, String>()
                            headers["Content-type"] = "application/json"
                            headers["token"] = "c6fb515078cf22"
                            return headers
                        }
                    }

                stack.add(jsonObjectRequest)
            }else{
                val dialog = android.app.AlertDialog.Builder(this@RestaurantMenu)
                dialog.setTitle("Error")
                dialog.setMessage("Internet Connection not Found")

                dialog.setPositiveButton("Open Settings") { text, listener ->
                    val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                    startActivity(settingsIntent)
                    finish()
                }
                dialog.setNegativeButton("Exit") { text, listener ->
                    ActivityCompat.finishAffinity(this@RestaurantMenu)
                }
                dialog.create()
                dialog.show()

            }
        }
        proceedTOCart.setOnClickListener {
            val intent =
                Intent(this@RestaurantMenu, CartActivity::class.java).putExtra("name", name)
                    .putExtra("id", restaurant_id)
            startActivity(intent)

        }

    }

    fun setUpToolBar() {
        setSupportActionBar(resname)
        supportActionBar?.title = name
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onBackPressed() {
        if (DBAsyncTask(this@RestaurantMenu,  1).execute().get()) {
            //create alert dialog
            val dialog = AlertDialog.Builder(this@RestaurantMenu)
            dialog.setTitle("Confirmation")
            dialog.setMessage("Going back will reset cart items. Do you still want to proceed?")

            dialog.setPositiveButton("YES") { text, listener ->
                //clear cart and go back
                if (DBAsyncTask(this@RestaurantMenu, 0).execute().get()) {
                    //cleared
                    Toast.makeText(
                        this@RestaurantMenu,
                        "Cart database cleared",
                        Toast.LENGTH_SHORT
                    ).show()

                    super.onBackPressed()
                } else {
                    Toast.makeText(
                        this@RestaurantMenu,
                        "Cart database not cleared",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            dialog.setNegativeButton("NO") { text, listener ->
                //clear cart and go back
                dialog.create().dismiss()
            }

            dialog.create()
            dialog.show()

        } else {
            super.onBackPressed()
        }
    }
    class DBAsyncTask(val context: Context, val mode: Int) : AsyncTask<Void, Void, Boolean>() {

        private val db = Room.databaseBuilder(context, ItemDataBase::class.java, "OrderCart-db").build()

        override fun doInBackground(vararg params: Void?): Boolean {
            when (mode) {
                //Remove all items from cart
                0 -> {
                    db.itemDao().clearCart()
                    db.close()
                    return true
                }
                //check if cart is empty or not
                1 -> {
                    val cartItems = db.itemDao().getAllItems()
                    db.close()
                    return cartItems.size > 0
                }
            }
            return false
        }


    }
}





