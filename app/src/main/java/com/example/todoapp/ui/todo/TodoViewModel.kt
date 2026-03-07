package com.example.todoapp.ui.todo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.todoapp.data.local.TodoEntity
import com.example.todoapp.data.repository.TodoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TodoViewModel(private val repository: TodoRepository) : ViewModel() {

    val todos: StateFlow<List<TodoEntity>> = repository.getAllTodos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _showAddDialog = MutableStateFlow(false)
    val showAddDialog: StateFlow<Boolean> = _showAddDialog.asStateFlow()

    private val _searchResults = MutableStateFlow<List<TodoEntity>>(emptyList())
    val searchResults: StateFlow<List<TodoEntity>> = _searchResults.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun showAddDialog() {
        _showAddDialog.value = true
    }

    fun dismissAddDialog() {
        _showAddDialog.value = false
    }

    fun addTodo(title: String, description: String = "") {
        if (title.isBlank()) return
        viewModelScope.launch {
            repository.addTodo(title, description)
        }
        _showAddDialog.value = false
    }

    fun toggleComplete(todo: TodoEntity) {
        viewModelScope.launch {
            repository.updateTodo(todo.copy(isCompleted = !todo.isCompleted))
        }
    }

    fun deleteTodo(todo: TodoEntity) {
        viewModelScope.launch {
            repository.deleteTodo(todo)
        }
    }

    fun searchTodos(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _searchResults.value = repository.searchTodos(query)
        }
    }

    fun deleteCompletedTodos() {
        viewModelScope.launch {
            val activeTodos = todos.value.filter { !it.isCompleted }
            repository.deleteCompletedAndReinsert(activeTodos)
        }
    }

    fun duplicateTodo(todo: TodoEntity) {
        viewModelScope.launch {
            repository.duplicateTodo(todo.id)
        }
    }

    fun bulkMarkComplete() {
        viewModelScope.launch(Dispatchers.Main) {
            val updated = todos.value.map { it.copy(isCompleted = true) }
            repository.bulkUpdate(updated)
        }
    }

    class Factory(private val repository: TodoRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return TodoViewModel(repository) as T
        }
    }
}
