package com.example.androidtaskmanager.models

class User (
    val Id: Int = 0,
    val Username: String = "default",
    val Lname: String = "default",
    val Role: Role = Role(0, "invalid Role"),
    val Scopes: MutableList<Scope> = mutableListOf()
)

class Role(
    val Id: Int,
    val Name: String
)