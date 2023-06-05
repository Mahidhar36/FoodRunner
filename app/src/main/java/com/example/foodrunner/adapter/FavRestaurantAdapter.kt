package com.example.foodrunner.adapter

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
import com.squareup.picasso.Picasso

class FavRestaurantAdapter(val context: Context, var restaurant_entity:MutableList<RestaurantEntity>):RecyclerView.Adapter<FavRestaurantAdapter.FavRestaurantViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavRestaurantViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_fav, parent, false)

        return FavRestaurantViewHolder(view)
    }



    override fun onBindViewHolder(holder: FavRestaurantViewHolder, position: Int) {
        val restaurant =restaurant_entity[position]
        holder.name.text = restaurant.resName
        holder.rating.text = restaurant.resRating
        holder.cost.text = "Rs.${restaurant.resCostForOne}/person"

        Picasso.get().load(restaurant.resImage).error(R.mipmap.ic_launcher).into(holder.imag)
        val resEntity = RestaurantEntity(
            restaurant.resId.toInt(),
            restaurant.resName,
            restaurant.resCostForOne,
            restaurant.resRating,
            restaurant.resImage
        )
         holder.ll2.setOnClickListener{
             val intent = Intent(context, RestaurantMenu::class.java).putExtra("id",restaurant.resId.toString())
             intent.putExtra("name", holder.name.text)

             context.startActivity(intent)
         }
        holder.imag1.setOnClickListener{
            //if isNotFav
            if (DBAsyncTask(holder.imag1.context, resEntity, 1).execute()
                    .get()
            ) {
                val async =
                    DBAsyncTask(holder.imag1.context, resEntity, 3).execute()


                val result = async.get()

                if (result) {
                    //if successfully added to favorites database..
                     holder.imag1.setImageResource(R.drawable.ic_fav_outline)
                    Toast.makeText(holder.imag1.context, "successfully removed from favourites", Toast.LENGTH_SHORT).show()
                      remove(restaurant)
                }

            //else if isFav


            }

        }
    }
fun remove(Res:RestaurantEntity){
    val rest:MutableList<RestaurantEntity> = restaurant_entity

    for(r in rest) {
   if(r==Res) {
       rest.remove(r)
   }
    }
    this.restaurant_entity=rest
    notifyDataSetChanged()


}

    override fun getItemCount(): Int {

        return restaurant_entity.size
    }
    class FavRestaurantViewHolder(view: View):RecyclerView.ViewHolder(view) {
        val name: TextView =view.findViewById(R.id.txtNameOfFavRestaurant)
        val cost: TextView =view.findViewById(R.id.txtFavCostPerPerson)
        val imag: ImageView =view.findViewById(R.id.imgFavRestaurant)
        val rating: TextView =view.findViewById(R.id.FavRestaurantRating)
        val imag1:ImageView=view.findViewById(R.id.FavouriteRes)
        val ll2:LinearLayout=view.findViewById(R.id.llContent2)



    }
    class DBAsyncTask(val context: Context, val resEntity: RestaurantEntity, val mode: Int) : AsyncTask<Void, Void, Boolean>() {

        /*
        Mode1->Check DB if book is favourite or not
        Mode2->Save the book into DB
        Mode3->Remove the favourite book
         */

        private val db =
            Room.databaseBuilder(context, RestaurantDataBase::class.java, "restaurant-db").build()

        override fun doInBackground(vararg params: Void?): Boolean {

            when (mode) {
                1 -> {
//                    Check DB if Restaurant is favourite or not
                    val res: RestaurantEntity? =
                        db.restaurantDao().getRestaurantById(resEntity.resId.toString())
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

}