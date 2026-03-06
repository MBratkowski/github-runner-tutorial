package com.example.todoapp.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {
    @Query("SELECT * FROM todos ORDER BY createdAt DESC")
    fun getAllTodos(): Flow<List<TodoEntity>>

    @Insert
    suspend fun insert(todo: TodoEntity)

    @Update
    suspend fun update(todo: TodoEntity)

    @Delete
    suspend fun delete(todo: TodoEntity)
}
