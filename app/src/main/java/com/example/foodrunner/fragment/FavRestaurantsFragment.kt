package com.example.foodrunner.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.foodrunner.R
import com.example.foodrunner.adapter.FavRestaurantAdapter
import com.example.foodrunner.database.RestaurantDataBase
import com.example.foodrunner.database.RestaurantEntity
import com.example.foodrunner.model.Restaurants


class FavRestaurantsFragment : Fragment() {
    lateinit var recycleFavourite: RecyclerView
    lateinit var progresslayout: RelativeLayout
    lateinit var progressbar: ProgressBar
    lateinit var recycleadapter:FavRestaurantAdapter
    lateinit var layoutmanager: LinearLayoutManager
    var dbReslist=  mutableListOf<RestaurantEntity>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view=  inflater.inflate(R.layout.fragment_fav_restaurants, container, false)




        recycleFavourite=view.findViewById(R.id.txtFavourite)
        progressbar=view.findViewById(R.id.ProgressBar2)
        progresslayout=view.findViewById(R.id.ProgressLayout3)
        layoutmanager= LinearLayoutManager(activity as Context)
        dbReslist = RetriveFavourites(activity as Context).execute()
            .get() as MutableList<RestaurantEntity>


        if(dbReslist.size==0){
            Toast.makeText(context, "This Page Is Empty", Toast.LENGTH_SHORT).show()
        }

        if(activity!=null){
            progresslayout.visibility=View.GONE
            recycleadapter=FavRestaurantAdapter(activity as Context,dbReslist)
            recycleFavourite.adapter=recycleadapter
            recycleFavourite.layoutManager=layoutmanager

        }
        return view

    }
    class RetriveFavourites(val context: Context): AsyncTask<Void, Void, List<RestaurantEntity>>() {
        @SuppressLint("SuspiciousIndentation")
        override fun doInBackground(vararg params: Void?): List<RestaurantEntity> {
            val db= Room.databaseBuilder(context, RestaurantDataBase::class.java,"restaurant-db").build()
            return db.restaurantDao().getAllRestaurant()
        }
    }



}
