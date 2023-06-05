package com.example.foodrunner.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodrunner.R
import com.example.foodrunner.adapter.OrderHistoryAdapter
import com.example.foodrunner.util.ConnectionManager
import com.google.gson.GsonBuilder
import org.json.JSONException


class OrderHistoryFragment : Fragment() {

    lateinit var recyclerRestaurant: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: OrderHistoryAdapter
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_order_history, container, false)
        recyclerRestaurant = view.findViewById(R.id.recyclerRestaurant)
        progressLayout = view.findViewById(R.id.progressLayout)
        progressBar = view.findViewById(R.id.progressBar)
        progressLayout.visibility = View.VISIBLE
        layoutManager = LinearLayoutManager(activity)


        val sharedPreferences = this.activity?.getSharedPreferences(
            "Preferences",
            Context.MODE_PRIVATE
        )


        val queue = Volley.newRequestQueue(activity as Context)

        val user_id = sharedPreferences?.getString("user_id", "DEFAULT_USER_ID")

        val url = "http://13.235.250.119/v2/orders/fetch_result/${user_id}"

        if (ConnectionManager().checkConnectivity(activity as Context)) {
            val jsonObjectRequest =

                object : JsonObjectRequest(
                    Request.Method.GET, url, null, Response.Listener {
                        val mainData = it.getJSONObject("data")

                        try {
                            progressLayout.visibility = View.GONE
                            val success = mainData.getBoolean("success")


                            if (success) {
                                val gson = GsonBuilder().create()
                                val orderHistory =
                                    gson.fromJson(mainData.toString(), OrderHistory::class.java)


                                //for recycler view part
                                recyclerAdapter =
                                    OrderHistoryAdapter(activity as Context, orderHistory.data)

                                recyclerRestaurant.adapter = recyclerAdapter
                                recyclerRestaurant.layoutManager = layoutManager


                            } else {
                                Toast.makeText(
                                    activity as Context,
                                    "Some Error Occurred!!!",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }

                        } catch (e: JSONException) {
                            Toast.makeText(
                                activity as Context,
                                "Some unexpected error occurred!!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }


                    },
                    Response.ErrorListener {
                        //Handle Errors
                        if (activity != null) {
                            Toast.makeText(
                                activity as Context,
                                "Volley error occurred!!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = "c6fb515078cf22"
                        return headers
                    }
                }


            queue.add(jsonObjectRequest)

        }else{
            val dialog = AlertDialog.Builder(activity as Context)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection not Found")

            dialog.setPositiveButton("Open Settings") { text, listener ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                activity?.finish()
            }
            dialog.setNegativeButton("Exit") { text, listener ->
                ActivityCompat.finishAffinity(activity as Activity)
            }
            dialog.create()
            dialog.show()

        }
            return view
        }




}


class OrderHistory(
    val data: List<Order>
)

class Order(
    val order_id: Int,
    val restaurant_name: String,
    val total_cost: Int,
    val order_placed_at: String,//try with date type also
    val food_items: List<FoodItem>
)

class FoodItem(
    val food_item_id: Int,
    val name: String,
    val cost: Int
)