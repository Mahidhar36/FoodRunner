package com.example.foodrunner.database

import androidx.room.*

@Entity(tableName = "OrderCart", primaryKeys = ["itemId", "resId"],
    indices = [Index(value = ["itemId"]), Index(value = ["resId"])])
data class ItemsEntity(
    val itemId: Int,
    val resId: String,
    @ColumnInfo(name = "item_name") val itemName: String,
    @ColumnInfo(name = "item_cost") val itemcost: String
)
