package com.example.foodrunner.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
@Dao
interface ItemDao {
    @Insert
    fun insertItem(item:ItemsEntity)

    @Delete
    fun deleteItem(item:ItemsEntity)

    @Query(value="DELETE FROM OrderCart")
    fun clearCart()

    @Query(value="SELECT * FROM OrderCart")
    fun getAllItems():List<ItemsEntity>

    @Query(value="SELECT * FROM OrderCart WHERE  itemId= :itemId ")
    fun getItemById(itemId:String ):ItemsEntity

    @Query(value="SELECT * FROM OrderCart WHERE  resId= :resId ")
    fun getItemByResName(resId:String ):List<ItemsEntity>

}