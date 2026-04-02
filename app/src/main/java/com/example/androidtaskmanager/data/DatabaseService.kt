package com.example.androidtaskmanager.data

import android.util.Log
import com.example.androidtaskmanager.models.Role
import com.example.androidtaskmanager.models.Scope
import com.example.androidtaskmanager.models.Status
import com.example.androidtaskmanager.models.Task
import com.example.androidtaskmanager.models.User
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.datetime.LocalDate
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Dictionary
import kotlin.math.log

class DatabaseService {
    private val _host = "5.129.192.69"
    private val _uri = "http://$_host/rmpPhpApi/api/"

    private val api = Retrofit.Builder()
        .baseUrl(_uri)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ApiService::class.java)

    suspend fun login(username: String, password: String): User{
        val data = JsonObject()
        data.addProperty("username", username)
        data.addProperty("password", password)
        val rootResponse = api.login(data)

        return try{
            var loginedUser = User()
            val res = rootResponse.get("success").asBoolean
            if (res){
                val usersRootResponse = api.getUsers()
                val usersResponse = usersRootResponse.get("user").asJsonArray
                for (userElement in usersResponse){
                    val userObject = userElement.asJsonObject
                    val user = User(
                        userObject.get("Id").asInt,
                        userObject.get("Username").asString,
                        userObject.get("Lname").asString,
                        Role(
                            userObject.get("RoleId").asInt, userObject.get("RoleName").asString
                        )
                    )
                    if (user.Username == username){
                        loginedUser = user
                        break
                    }
                }
                loginedUser
            }
            else {
                throw Exception("login incorrect!")
            }
        }
        catch (e: Exception){
            throw Exception("error in DatabaseService, loginUser()", e)
        }

    }
    suspend fun getTasks(): MutableList<Task>{
        val rootResponseJson = api.getTasks()
        val tasks = mutableListOf<Task>()
        if (rootResponseJson.get("success").asBoolean){
            if (rootResponseJson.get("count").asInt > 0){
                return try{
                    val tasksResponse = rootResponseJson.getAsJsonArray("data")
                    for (t in tasksResponse){
                        val taskResponse = t.asJsonObject
                        println(taskResponse.toString())
                        val task = Task(
                            taskResponse.get("Id").asInt,
                            User(
                                taskResponse.get("IdOwner").asInt,
                                taskResponse.get("OwnerUsername").asString,
                                taskResponse.get("OwnerLname").asString,
                                Role(
                                    taskResponse.get("OwnerIdRole").asInt,
                                    taskResponse.get("OwnerRoleName").asString,
                                )
                            ),
                            Status(
                                taskResponse.get("IdStatus").asInt,
                                taskResponse.get("StatusName").asString
                            ),
                            taskResponse.get("Title").asString,
                            taskResponse.get("Description")?.takeIf { !it.isJsonNull }?.asString ?: "Без описания",
                            Scope(taskResponse.get("IdScope").asInt, taskResponse.get("ScopeName").asString),
                            LocalDate.parse(taskResponse.get("Since").asString),
                            LocalDate.parse(taskResponse.get("Deadline").asString)
                        )
                        tasks.add(task)
                    }
                    tasks
                } catch (e: Exception){
                    throw Exception(e.message.toString())
                }
            }
            else {
                return mutableListOf()
            }
        }
        else {
            throw Exception("success == false")
        }
    }
    suspend fun getUsers(): MutableList<User>{ // need add scopes (responsibilities)
        val rootResponseJson = api.getUsers()
        if (rootResponseJson.get("success").asBoolean){
            return try{
                val users = mutableListOf<User>()

                val responsibilitiesResponse = rootResponseJson.getAsJsonArray("responsibility")
                val usersResponse = rootResponseJson.getAsJsonArray("user")

                for (u in usersResponse){
                    val userResponse = u.asJsonObject
                    val user = User(
                        userResponse.get("Id").asInt,
                        userResponse.get("Username").asString,
                        userResponse.get("Lname").asString,
                        Role(
                            userResponse.get("RoleId").asInt,
                            userResponse.get("RoleName").asString
                        )
                    )
                    users.add(user)
                }

                for (u in users){
                    val userScopes = mutableListOf<Scope>()
                    for (r in responsibilitiesResponse){
                        val responsibilityResponse = r.asJsonObject
                        if (u.Id == responsibilityResponse.get("idResponsibleUser").asInt){
                            val scope = getScope(
                                responsibilityResponse.get("idScope").asInt
                            )
                            userScopes.add(scope)
                        }
                    }
                    u.Scopes.addAll(userScopes)
                }

                users
            } catch (e: Exception){
                println(e.message.toString())
                throw Exception(e.message)
            }
        }
        else {
            throw Exception("rootResponse['success'] == false")
        }
    }
    suspend fun getScope(id: Int): Scope{
        val data = JsonObject()
        data.addProperty("id", id)
        val rootResponseJson = api.getScope(data)
        return try {
            if (rootResponseJson.get("scope").isJsonObject){
                val scopeResponse = rootResponseJson.get("scope").asJsonObject
                Scope(
                    scopeResponse.get("id").asInt,
                    scopeResponse.get("name").asString
                )
            }
            else {
                throw Exception("scope == false")
            }
        } catch (e: Exception){
            throw Exception(e.message)
        }
    }
    suspend fun sendTaskForVerify(task: Task): Boolean{
        val data = JsonObject()
        data.addProperty("table_name", "task")
        data.addProperty("field_name", "idStatus")
        data.addProperty("field_new_value", "3")
        data.addProperty("id", task.Id)

        val rootResponseJson = api.sendTaskForVerify(data)
        return try{
            val r = rootResponseJson.get("success").asBoolean
            if (r){
                r
            }
            else {
                throw Exception("DatabaseService.sendTaskForVerify() error. r == false")
            }
        } catch (e: Exception){
            Log.e("DatabaseService.sendTaskForVerify()", e.message.toString())
            throw e
        }
    }
    suspend fun verifyTask(task: Task, userId: Int): Boolean{
        val data = JsonObject()
        data.addProperty("table_name", "task")
        data.addProperty("field_name", "idStatus")
        data.addProperty("field_new_value", "2")
        data.addProperty("id", task.Id)

        val rootResponseJson = api.sendTaskForVerify(data)
        return try{
            val r = rootResponseJson.get("success").asBoolean
            if (r){
                return putFinishedTask(task, userId)
            }
            else {
                throw Exception("DatabaseService.verifyTask() error. r == false")
            }
        } catch (e: Exception){
            Log.e("DatabaseService.verifyTask()", e.message.toString())
            throw e
        }
    }
    private suspend fun putFinishedTask(task: Task, userId: Int): Boolean{
        val data = JsonObject()
        data.addProperty("idTask", task.Id)
        data.addProperty("idFinishedUser", userId)

        val rootResponseJson = api.putFinishedTask(data)
        return try{
            val r = rootResponseJson.get("success").asBoolean
            if (r){
                r
            }
            else {
                throw Exception("DatabaseService.putFinishedTask() error. r == false")
            }
        } catch (e: Exception){
            Log.e("DatabaseService.putFinishedTask()", e.message.toString())
            throw e
        }
    }
}