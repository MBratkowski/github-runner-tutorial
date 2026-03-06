package com.example.todoapp.ui.todo

import com.example.todoapp.data.local.TodoEntity
import com.example.todoapp.data.repository.TodoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TodoViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeRepository: FakeTodoRepository
    private lateinit var viewModel: TodoViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeTodoRepository()
        viewModel = TodoViewModel(fakeRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state has empty todo list`() = runTest {
        val todos = viewModel.todos.first()
        assertTrue(todos.isEmpty())
    }

    @Test
    fun `showAddDialog updates state`() = runTest {
        assertFalse(viewModel.showAddDialog.first())
        viewModel.showAddDialog()
        assertTrue(viewModel.showAddDialog.first())
    }

    @Test
    fun `dismissAddDialog updates state`() = runTest {
        viewModel.showAddDialog()
        viewModel.dismissAddDialog()
        assertFalse(viewModel.showAddDialog.first())
    }

    @Test
    fun `addTodo with blank title does nothing`() = runTest {
        viewModel.addTodo("   ")
        testDispatcher.scheduler.advanceUntilIdle()
        assertTrue(fakeRepository.todos.value.isEmpty())
    }

    @Test
    fun `addTodo adds item to repository`() = runTest {
        viewModel.addTodo("Test Todo", "Test Description")
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(1, fakeRepository.todos.value.size)
        assertEquals("Test Todo", fakeRepository.todos.value[0].title)
    }

    @Test
    fun `addTodo dismisses dialog`() = runTest {
        viewModel.showAddDialog()
        viewModel.addTodo("Test")
        assertFalse(viewModel.showAddDialog.first())
    }
}

class FakeTodoRepository : TodoRepository(FakeTodoDao()) {
    val todos = MutableStateFlow<List<TodoEntity>>(emptyList())
    private var nextId = 1L

    override fun getAllTodos(): Flow<List<TodoEntity>> = todos

    override suspend fun addTodo(title: String, description: String) {
        val todo = TodoEntity(id = nextId++, title = title, description = description)
        todos.value = todos.value + todo
    }

    override suspend fun updateTodo(todo: TodoEntity) {
        todos.value = todos.value.map { if (it.id == todo.id) todo else it }
    }

    override suspend fun deleteTodo(todo: TodoEntity) {
        todos.value = todos.value.filter { it.id != todo.id }
    }
}

private class FakeTodoDao : com.example.todoapp.data.local.TodoDao {
    override fun getAllTodos(): Flow<List<TodoEntity>> = MutableStateFlow(emptyList())
    override suspend fun insert(todo: TodoEntity) {}
    override suspend fun update(todo: TodoEntity) {}
    override suspend fun delete(todo: TodoEntity) {}
}
