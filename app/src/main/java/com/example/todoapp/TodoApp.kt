package com.example.todoapp

import android.app.Application
import com.example.todoapp.di.AppContainer

class TodoApp : Application() {
    lateinit var appContainer: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        appContainer = AppContainer(this)
    }
}
