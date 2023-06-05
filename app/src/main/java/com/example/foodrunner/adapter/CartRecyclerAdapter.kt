package com.example.foodrunner.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.foodrunner.R
import com.example.foodrunner.database.ItemsEntity

import com.example.foodrunner.database.RestaurantEntity

class CartRecyclerAdapter(val context: Context, var item_entity: ArrayList<ItemsEntity>): RecyclerView.Adapter<CartRecyclerAdapter.CartRecyclerViewHolder>() {
    class CartRecyclerViewHolder(view: View):RecyclerView.ViewHolder(view) {
   val text:TextView=view.findViewById(R.id.ItemCost1)
        val text1:TextView=view.findViewById(R.id.ItemName1)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartRecyclerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_cart_items, parent, false)

        return CartRecyclerAdapter.CartRecyclerViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartRecyclerViewHolder, position: Int) {


        val item = item_entity[position]


            holder.text1.text = item.itemName
            holder.text.text = "Rs+${item.itemcost}"


    }

    override fun getItemCount(): Int {
        return item_entity.size
    }
}