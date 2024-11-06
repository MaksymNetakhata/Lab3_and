package com.example.lab3_and.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.lab3_and.models.Item

@Dao
interface ItemDao {

    @Insert
    suspend fun insert(item: Item)

    @Query("SELECT * FROM items")
    suspend fun getAllItems(): List<Item>

    @Delete
    suspend fun delete(item: Item)
}