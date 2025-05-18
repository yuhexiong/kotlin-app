package com.example.shoppinglistapplication.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.shoppinglistapplication.data.dao.ShoppingItemDao
import com.example.shoppinglistapplication.data.entity.ShoppingItem

@Database(entities = [ShoppingItem::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun shoppingItemDao(): ShoppingItemDao
}