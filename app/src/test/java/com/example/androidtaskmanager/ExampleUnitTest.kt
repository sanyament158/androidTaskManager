package com.example.androidtaskmanager

import org.junit.Test

import org.junit.Assert.*
import com.example.androidtaskmanager.data.DatabaseService
import kotlin.coroutines.coroutineContext
import kotlinx.coroutines.test.runTest

class ExampleUnitTest {
    val db = DatabaseService()

    @Test
    fun getTasksTest() = runTest {
        val tasks = db.getTasks()
        if (tasks.count() > 0) {
            for (t in tasks) {
                println("${t.Id} --- ${t.Owner.Username} --- ${t.Scope.Name} --- ${t.Title}")
            }
            assertTrue(true)
        }
    }

    @Test
    fun getUsersTest() = runTest {
        val users = db.getUsers()
        println(users.count())
        for (u in users) {
            println("${u.Id}, ${u.Username}, ${u.Role.Name}, ${u.Scopes.count()}")
        }
        assertTrue(users.count() > 0)
    }

    @Test
    fun getScopeTest() = runTest {
        val scope = db.getScope(35)
        println("${scope.Id}, ${scope.Name}")
        assertTrue(scope.Id != 0)
    }

    @Test
    fun getScopesTest() = runTest {
        val scopes = db.getScopes()
        println("array count = ${scopes.count()}")
        for (u in scopes) {
            println("$${u.Id}, ${u.Name}")
        }
        assertTrue(scopes.count() > 0)
    }
}