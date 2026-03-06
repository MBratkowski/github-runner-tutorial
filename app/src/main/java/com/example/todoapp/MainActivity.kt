package com.example.todoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.todoapp.ui.theme.TodoAppTheme
import com.example.todoapp.ui.todo.TodoScreen
import com.example.todoapp.ui.todo.TodoViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val app = application as TodoApp
        setContent {
            TodoAppTheme {
                val viewModel: TodoViewModel = viewModel(
                    factory = TodoViewModel.Factory(app.appContainer.todoRepository)
                )
                TodoScreen(viewModel = viewModel)
            }
        }
    }
}
