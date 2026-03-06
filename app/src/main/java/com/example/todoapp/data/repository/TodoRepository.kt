package com.example.todoapp.data.repository

import com.example.todoapp.data.local.TodoDao
import com.example.todoapp.data.local.TodoEntity
import kotlinx.coroutines.flow.Flow

open class TodoRepository(private val todoDao: TodoDao) {
    open fun getAllTodos(): Flow<List<TodoEntity>> = todoDao.getAllTodos()

    open suspend fun addTodo(title: String, description: String = "") {
        todoDao.insert(TodoEntity(title = title, description = description))
    }

    open suspend fun updateTodo(todo: TodoEntity) {
        todoDao.update(todo)
    }

    open suspend fun deleteTodo(todo: TodoEntity) {
        todoDao.delete(todo)
    }
}
