package com.example.foodrunner.adapter

import android.content.Context
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.foodrunner.R
import com.example.foodrunner.database.ItemDataBase
import com.example.foodrunner.database.ItemsEntity
import com.example.foodrunner.model.RestaurantMenu

class RestaurantMenuAdapter(val context: Context, var itemlist: ArrayList<RestaurantMenu>): RecyclerView.Adapter<RestaurantMenuAdapter.RestaurantMenuViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantMenuViewHolder {

            val view= LayoutInflater.from(parent.context).inflate(R.layout.recyclervie_items,parent,false)
            return RestaurantMenuViewHolder(view)
    }

    override fun onBindViewHolder(holder: RestaurantMenuViewHolder, position: Int) {
        val item=itemlist[position]
        holder.text1.text=item.ItemName
        holder.text2.text="Rs.${item.cost_for_one}/Person"
        holder.index.text=position.toString()


        val itemsEntity = ItemsEntity(
            item.ItemId.toInt(),
            item.RestaurantId.toString(),

            item.ItemName,
            item.cost_for_one

        )
       val checkFav=DBAsyncTask(context,itemsEntity,1).execute()
        val isFav=checkFav.get()
        if(isFav){
           holder.add.text="Remove"
            val favcolor= ContextCompat.getColor(context,R.color.red)
            holder.add.setBackgroundColor(favcolor)
        }else{
            holder.add.text="Add"
            val favcolor= ContextCompat.getColor(context,R.color.green)
            holder.add.setBackgroundColor(favcolor)
        }

        holder.ll.setOnClickListener{

        }
        holder.add.setOnClickListener{
            if (!DBAsyncTask(holder.add.context, itemsEntity, 1).execute()
                    .get()
            ) {
                //add to cart

                val async = DBAsyncTask(holder.add.context, itemsEntity, 2).execute()
                val result = async.get()

                //if successfully added to cart
                if (result) {
                    //change text of button to "Remove"
                    holder.add.text = "Remove"
                    //change background of button to "Yellow"
                    val removeColor = ContextCompat.getColor(context, R.color.red)
                    holder.add.setBackgroundColor(removeColor)
                } else {
                    //some error occurred while adding to database
                    Toast.makeText(context, "Some error occurred", Toast.LENGTH_SHORT).show()
                }
            }
//            else remove from cart
            else {
                //remove from cart
                //CHECK
                //if successfully removed from cart
                if (DBAsyncTask(context, itemsEntity, 3).execute().get()) {
                    //change text of button to "Add"
                    holder.add.text = "Add"
                    //change background of button to "Primary Color"
                    val addColor = ContextCompat.getColor(context, R.color.green)
                    holder.add.setBackgroundColor(addColor)
                } else {
                    //some error occurred while adding to database
                    Toast.makeText(context, "Some error occurred", Toast.LENGTH_SHORT).show()
                }

            }



        }
        }


    override fun getItemCount(): Int {
        return itemlist.size
    }

    class RestaurantMenuViewHolder(view: View):RecyclerView.ViewHolder(view) {
        val text1: TextView =view.findViewById(R.id.ItemName)
        val text2: TextView =view.findViewById(R.id.ItemCost)
        val index: TextView =view.findViewById(R.id.Index)
        val add: Button =view.findViewById(R.id.AddToCart)
        val ll: LinearLayout =view.findViewById(R.id.LinearLayout)
    }
    class DBAsyncTask(val context: Context, val itemEntity: ItemsEntity, val mode: Int) : AsyncTask<Void, Void, Boolean>() {


        private val db = Room.databaseBuilder(context, ItemDataBase::class.java, "OrderCart-db").build()



        override fun doInBackground(vararg params: Void?): Boolean {

            when (mode) {
                1 -> {
//                    Check DB if item is in cart or not
                    val item: ItemsEntity?= db.itemDao().getItemById(itemEntity.itemId.toString())
                    db.close()
                    return item != null
                }
                2 -> {
//                    Add the item into cart
                    db.itemDao().insertItem(itemEntity)
                    db.close()
                    return true
                }
                3 -> {
//                    Remove the item from cart
                    db.itemDao().deleteItem(itemEntity)
                    db.close()
                    return true
                }
                4 -> {
//                    Get all items and check it the length of list is 0 or not
                    val cartItems = db.itemDao().getAllItems()
                    db.close()
                    return cartItems.size > 0
                }
                5->{
                    val item=db.itemDao().getItemByResName(itemEntity.resId)
                    db.close()
                        return item.size>0

                }
            }

            return false
        }


    }
}