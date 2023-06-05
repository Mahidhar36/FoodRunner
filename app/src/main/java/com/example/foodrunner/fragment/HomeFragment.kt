package com.example.foodrunner.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodrunner.R
import com.example.foodrunner.adapter.HomeRecyclerAdapter
import com.example.foodrunner.model.Restaurants
import com.example.foodrunner.util.ConnectionManager
import java.util.*


class HomeFragment : Fragment() {

    lateinit var recyclerRestaurants: RecyclerView
    lateinit var layoutManager: LinearLayoutManager
    lateinit var recyclerAdapter: HomeRecyclerAdapter
     var RestaurantList= arrayListOf<Restaurants>()
    lateinit var progresslayout: RelativeLayout
    lateinit var progressBar: ProgressBar

   lateinit var search:EditText


    val ratingComparator= Comparator<Restaurants> {restaurant1, restaurant2 ->
        restaurant1.restaurantRating.compareTo(restaurant2.restaurantRating,true)
    }
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       setHasOptionsMenu(true)
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        recyclerRestaurants = view.findViewById(R.id.RecyclerRestaurants)
        layoutManager = LinearLayoutManager(activity)
         search=view.findViewById(R.id.SearchRestaurant)

        progresslayout=view.findViewById(R.id.ProgressLayout1)
        progressBar=view.findViewById(R.id.Progressbar)
        progresslayout.visibility=View.VISIBLE
        progressBar.visibility=View.VISIBLE
        search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                filter(s.toString())
            }
        })

        val stack = Volley.newRequestQueue(activity as Context)
        val url = "http://13.235.250.119/v2/restaurants/fetch_result/"
        if(ConnectionManager().checkConnectivity(activity as Context)) {
            val jsonObjectRequest =
                object : JsonObjectRequest(Request.Method.GET, url, null, Response.Listener {
                    val data = it.getJSONObject("data")
                    val success = data.getBoolean("success")
                    if (success) {
                        progresslayout.visibility = View.GONE
                        val data1 = data.getJSONArray("data")

                        for (i in 0 until data1.length()) {
                            val datarestaurantObject = data1.getJSONObject(i)
                            val restaurantObject = Restaurants(
                                datarestaurantObject.getInt("id"),
                                datarestaurantObject.getString("name"),
                                datarestaurantObject.getString("cost_for_one"),
                                datarestaurantObject.getString("rating"),

                                datarestaurantObject.getString("image_url")
                            )
                            RestaurantList.add(restaurantObject)

                            recyclerAdapter =
                                HomeRecyclerAdapter(activity as Context, RestaurantList)
                            recyclerRestaurants.adapter = recyclerAdapter
                            recyclerRestaurants.layoutManager = layoutManager
                        }

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

        }
        else{
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
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_sort,menu)


}

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id=item?.itemId
        if(id==R.id.Sort){
            Collections.sort(RestaurantList,ratingComparator)
            RestaurantList.reverse()

        }
        recyclerAdapter.notifyDataSetChanged()
        return super.onOptionsItemSelected(item)
    }
    private fun filter(text: String) {
        //new array list that will hold the filtered data
        val filterdNames= ArrayList<Restaurants>()

        //looping through existing elements
        for (s in RestaurantList) {
            //if the existing elements contains the search input
            if (s.restaurantName.lowercase(Locale.getDefault()).contains(text.lowercase(Locale.getDefault()))) {
                //adding the element to filtered list
                filterdNames.add(s)
            }
        }

        //calling a method of the adapter class and passing the filtered list
        recyclerAdapter.filterList(filterdNames)
    }



}