package com.example.foodrunner.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.foodrunner.model.Restaurants

@Dao
interface RestaurantDao {
    @Insert
    fun insertRestaurant(restaurant:RestaurantEntity)
    @Delete
    fun deleteRestaurant(restaurant:RestaurantEntity)
    @Query(value="SELECT * FROM restaurants")
    fun getAllRestaurant():List<RestaurantEntity>
    @Query(value="SELECT * FROM restaurants WHERE resId= :res_Id")

    fun getRestaurantById(res_Id:String ):RestaurantEntity

}