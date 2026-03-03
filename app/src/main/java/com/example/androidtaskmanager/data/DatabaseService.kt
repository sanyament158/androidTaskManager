package com.example.androidtaskmanager.data

import android.util.Log
import com.example.androidtaskmanager.models.Role
import com.example.androidtaskmanager.models.User
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
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
            Log.e("error in DatabaseService, loginUser()", e.message.toString())
            throw Exception("error in DatabaseService, loginUser()", e)
        }

    }
}