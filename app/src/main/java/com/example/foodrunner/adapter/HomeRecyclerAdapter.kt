package com.example.foodrunner.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.AsyncTask

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.foodrunner.R
import com.example.foodrunner.activity.RestaurantMenu
import com.example.foodrunner.database.RestaurantDataBase
import com.example.foodrunner.database.RestaurantEntity
import com.example.foodrunner.model.Restaurants
import com.squareup.picasso.Picasso

class HomeRecyclerAdapter(val context: Context, var restaurantList: ArrayList<Restaurants>) :RecyclerView.Adapter<HomeRecyclerAdapter.HomeViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_singlerow_home, parent, false)
        return HomeViewHolder(view)
    }




    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val restaurant = restaurantList[position]
        holder.name.text = restaurant.restaurantName
        holder.rating.text = restaurant.restaurantRating
        holder.cost.text = "Rs.${restaurant.restaurantFoodCost}/person"

        Picasso.get().load(restaurant.restaurantImage).error(R.mipmap.ic_launcher).into(holder.imag)

        val resEntity = RestaurantEntity(
            restaurant.restaurantId.toInt(),
            restaurant.restaurantName,
            restaurant.restaurantFoodCost,
            restaurant.restaurantRating,
            restaurant.restaurantImage
        )
        val checkFav = DBAsyncTask(context, resEntity, 1).execute()
        val isFav = checkFav.get()

        if (isFav) {

            holder.imag1.setImageResource(R.drawable.ic_fav_heart)


        } else {

            holder.imag1.setImageResource(R.drawable.ic_fav_outline)
        }
        holder.imag1.setOnClickListener {

            //if isNotFav
            if (!DBAsyncTask(holder.imag1.context, resEntity, 1).execute()
                    .get()
            ) {
                val async =
                    DBAsyncTask(holder.imag1.context, resEntity, 2).execute()


                val result = async.get()

                if (result) {
                    //if successfully added to favorites database..
                    holder.imag1.setImageResource(R.drawable.ic_fav_heart)
                    Toast.makeText(holder.imag1.context, "successfully added to favourites", Toast.LENGTH_SHORT).show()
                } else {
                    //some error occurred while adding to database
                }
            }

            //else if isFav
            else {
                val async =
                    DBAsyncTask(holder.imag1.context, resEntity, 3).execute()
                val result = async.get()

                //if successfully removed from favorite database
                if (result) {
                    holder.imag1.setImageResource(R.drawable.ic_fav_outline)
                    Toast.makeText(holder.imag1.context, "successfully removed from favourites", Toast.LENGTH_SHORT).show()
                } else {
                    //some error occurred while removing from database
                }

            }

        }





            holder.llcontent1.setOnClickListener {

                val intent = Intent(context, RestaurantMenu::class.java).putExtra("id",restaurant.restaurantId.toString()).putExtra("name",restaurant.restaurantName)


                context.startActivity(intent)
            }


    }
        override fun getItemCount(): Int {
            return restaurantList.size
        }

        fun filterList(filterdNames: ArrayList<Restaurants>) {
            this.restaurantList = filterdNames
            notifyDataSetChanged()
        }

    class HomeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.txtNameOfRestaurant)
        val cost: TextView = view.findViewById(R.id.txtCostPerPerson)
        val imag: ImageView = view.findViewById(R.id.imgRestaurant)
        val rating: TextView = view.findViewById(R.id.RestaurantRating)
        val imag1: ImageView = view.findViewById(R.id.Favourites)
        val llcontent1: LinearLayout = view.findViewById(R.id.llContent1)
    }





    }
class DBAsyncTask(val context: Context, val resEntity: RestaurantEntity, val mode: Int) : AsyncTask<Void, Void, Boolean>() {

    /*
    Mode1->Check DB if book is favourite or not
    Mode2->Save the book into DB
    Mode3->Remove the favourite book
     */

    private val db = Room.databaseBuilder(context, RestaurantDataBase::class.java, "restaurant-db").build()

    override fun doInBackground(vararg params: Void?): Boolean {

        when (mode) {
            1 -> {
//                    Check DB if Restaurant is favourite or not
                val res: RestaurantEntity? = db.restaurantDao().getRestaurantById(resEntity.resId.toString())
                db.close()
                return res != null
            }
            2 -> {
//                    Save the Restaurant into DB
                db.restaurantDao().insertRestaurant(resEntity)
                db.close()
                return true
            }
            3 -> {
//                    Remove the favourite restaurant
                db.restaurantDao().deleteRestaurant(resEntity)
                db.close()
                return true
            }
        }

        return false
    }
}





