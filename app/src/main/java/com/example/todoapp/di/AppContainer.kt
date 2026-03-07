package com.example.todoapp.di

import android.content.Context
import com.example.todoapp.data.local.TodoDatabase
import com.example.todoapp.data.repository.TodoRepository

class AppContainer(private val context: Context) {
    private val database by lazy { TodoDatabase.getInstance(context) }
    val todoRepository by lazy { TodoRepository(database.todoDao()) }

    fun createNewRepository(): TodoRepository {
        return TodoRepository(TodoDatabase.getInstance(context).todoDao())
    }
}
