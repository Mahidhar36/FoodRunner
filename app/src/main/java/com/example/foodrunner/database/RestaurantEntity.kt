package com.example.foodrunner.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName="restaurants")
data class RestaurantEntity(

    @PrimaryKey val resId:Int,
    @ColumnInfo( name="res_name") val resName:String,
    @ColumnInfo(name="res_cost_for_one")val resCostForOne:String,
    @ColumnInfo(name="res_rating") val resRating:String,

    @ColumnInfo(name="res_image") val resImage:String

)
