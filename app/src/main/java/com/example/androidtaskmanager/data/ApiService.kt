package com.example.androidtaskmanager.data


import com.google.gson.JsonObject
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @POST("login/login.php")
    suspend fun login(@Body data: JsonObject): JsonObject

    @GET("getTable/getTasks.php")
    suspend fun getTasks(): JsonObject

    @GET("getTable/getUsers.php")
    suspend fun getUsers(): JsonObject

    @GET("getTable/getScopes.php")
    suspend fun getScopes(): JsonObject

    @POST("scope/getScopeById.php")
    suspend fun getScope(@Body data: JsonObject): JsonObject

    @POST("generics/updateFieldFromTableById.php")
    suspend fun sendTaskForVerify(@Body data: JsonObject): JsonObject

    @POST("generics/updateFieldFromTableById.php")
    suspend fun verifyTask(@Body data: JsonObject): JsonObject

    @POST("task/putFinishedTask.php")
    suspend fun putFinishedTask(@Body data: JsonObject): JsonObject

    @POST("task/putTask.php")
    suspend fun putTask(@Body data: JsonObject): JsonObject

}