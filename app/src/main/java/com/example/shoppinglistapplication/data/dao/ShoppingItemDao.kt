package com.example.shoppinglistapplication.data.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.example.shoppinglistapplication.data.entity.ShoppingItem

@Dao
interface ShoppingItemDao {

    @Query("SELECT * FROM shopping_items ORDER BY id ASC")
    fun getAllItems(): Flow<List<ShoppingItem>>

    @Insert
    suspend fun insertItem(item: ShoppingItem)

    @Update
    suspend fun updateItem(item: ShoppingItem)

    @Delete
    suspend fun deleteItem(item: ShoppingItem)
}
