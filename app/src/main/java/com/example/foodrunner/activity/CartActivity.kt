package com.example.foodrunner.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodrunner.R
import com.example.foodrunner.adapter.CartRecyclerAdapter

import com.example.foodrunner.database.*
import com.example.foodrunner.model.RestaurantMenu
import com.example.foodrunner.util.ConnectionManager
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import kotlin.properties.Delegates


class CartActivity : AppCompatActivity() {
    lateinit var res: androidx.appcompat.widget.Toolbar
    lateinit var recycleritems: RecyclerView
    lateinit var recyclerAdapter:CartRecyclerAdapter
    lateinit var progresslayout: RelativeLayout
    lateinit var progressBar: ProgressBar
    lateinit var layoutManager: LinearLayoutManager
    lateinit var order: Button
    var dbItem = arrayListOf<ItemsEntity>()
    var id by Delegates.notNull<Int>()
  var fooditems=ArrayList<Map<String,String>>()
    lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)
        recycleritems = findViewById(R.id.RecyclerCartItems)
        res = findViewById(R.id.Toolbar1)
        order=findViewById(R.id.PlaceOrder)
        layoutManager=LinearLayoutManager(this@CartActivity)
        progresslayout = findViewById(R.id.ProgressLayout3)
        progressBar = findViewById(R.id.Progressbar2)
        progresslayout.visibility = View.VISIBLE
        progressBar.visibility = View.VISIBLE
          sharedPreferences=getSharedPreferences("Preferences",Context.MODE_PRIVATE)
         var name1:String=""
        if(intent!=null) {
            val name: String? = intent.getStringExtra("name")

            name1=name.toString()
            id = intent.getIntExtra("id", 0)

            setUpToolBar()



            dbItem = RetriveItems1(this@CartActivity,id).execute().get() as ArrayList<ItemsEntity>
            for(item in dbItem){
                fooditems.add(mapOf("food_item_id" to "${item.itemId}"))
            } }
        val context:Context=this@CartActivity
        if(context!=null){
            var sum:Int=0
            for(item in dbItem){
                sum=sum+item.itemcost.toInt()
            }
            progresslayout.visibility=View.GONE
            recyclerAdapter= CartRecyclerAdapter(this@CartActivity,dbItem)
            recycleritems.adapter=recyclerAdapter
            recycleritems.layoutManager=layoutManager
            order.text="Place Order(Rs.${sum})"
            order.setOnClickListener {


                if (sum == 0) {
                    Toast.makeText(this@CartActivity, "Order Cannot be Empty", Toast.LENGTH_SHORT)
                        .show()
                    order.setEnabled(false)
                } else {
                    val jsonArray = JSONArray()
                    for (item in dbItem) {
                        val jsonobject = JSONObject()
                        jsonobject.put("food_item_id", "${item.itemId}")
                        jsonArray.put(jsonobject)
                    }
                    progresslayout.visibility = View.VISIBLE
                    progressBar.visibility = View.VISIBLE
                    val url = "http://13.235.250.119/v2/place_order/fetch_result/"
                    val queue = Volley.newRequestQueue(this@CartActivity)
                    if (ConnectionManager().checkConnectivity(this@CartActivity)) {
                        val jsonParams = JSONObject()
                        jsonParams.put(
                            "user_id",
                            sharedPreferences.getString("user_id", "DEFAULT_NAME").toString()
                        )
                        jsonParams.put("restaurant_id", id.toString())
                        jsonParams.put("total_cost", sum.toString())
                        jsonParams.put("food", jsonArray)
                        val jsonObjectRequest =
                            object : JsonObjectRequest(Request.Method.POST, url, jsonParams,
                                Response.Listener {
                                    try {
                                        val mainData = it.getJSONObject("data")
                                        val success = mainData.getBoolean("success")
                                        if (success) {

                                            progresslayout.visibility = View.GONE
                                            progressBar.visibility = View.GONE
                                            val t = DBAsyncTask(this@CartActivity, 0).execute()
                                            val intent = Intent(
                                                this@CartActivity,
                                                OrderPlacedActivity::class.java
                                            )
                                            context.startActivity(intent)

                                        } else {
                                            Toast.makeText(
                                                this@CartActivity,
                                                "Order Not Placed",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    } catch (e: JSONException) {
                                        Toast.makeText(
                                            this@CartActivity,
                                            "Some unexpected error occurred!!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }, Response.ErrorListener {
                                    Toast.makeText(
                                        this@CartActivity,
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
                        queue.add(jsonObjectRequest)
                    }
                    else{
                        val dialog = AlertDialog.Builder(this@CartActivity)
                        dialog.setTitle("Error")
                        dialog.setMessage("Internet Connection not Found")

                        dialog.setPositiveButton("Open Settings") { text, listener ->
                            val settingsIntent = Intent(Settings.ACTION_WIFI_SETTINGS)
                            startActivity(settingsIntent)
                            finish()
                        }
                        dialog.setNegativeButton("Exit") { text, listener ->
                            ActivityCompat.finishAffinity(this@CartActivity)
                        }
                        dialog.create()
                        dialog.show()
                    }
                }

            }

        }
    }
    class RetriveItems1(val context: Context,val resId: Int): AsyncTask<Void, Void, List<ItemsEntity>>() {
        @SuppressLint("SuspiciousIndentation")
        override fun doInBackground(vararg params: Void?): List<ItemsEntity> {
            val db= Room.databaseBuilder(context, ItemDataBase::class.java,"OrderCart-db").build()
            return db.itemDao().getItemByResName("${resId}")
        }
    }
    fun setUpToolBar() {
        setSupportActionBar(res)
        supportActionBar?.title = "My Cart"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
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

