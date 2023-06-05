package com.example.foodrunner.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities =[ItemsEntity::class],version=1)
abstract class ItemDataBase :RoomDatabase(){
    abstract fun itemDao():ItemDao
}