package com.example.androidtaskmanager.models

class Task(
    val Id: Int = 0,
    val Owner: User = User(),
    val Status: Status = Status(),
    val Title: String = "default",
    val Description: String = "Без описания",
    val Scope: Scope = Scope(),
    //since, deadline
)

class Scope(
    Id: Int = 0,
    Name: String = "default"
)
class Status(
    Id: Int = 0,
    Name: String = "default"
)