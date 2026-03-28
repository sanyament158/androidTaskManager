package com.example.androidtaskmanager.models
import kotlinx.datetime.*

class Task(
    val Id: Int = 0,
    val Owner: User = User(),
    val Status: Status = Status(),
    val Title: String = "default",
    val Description: String = "Без описания",
    val Scope: Scope = Scope(),
    val since: LocalDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
    val deadline: LocalDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.plus(1, DateTimeUnit.DAY)
)

class Scope(
    Id: Int = 0,
    Name: String = "default"
)
class Status(
    Id: Int = 0,
    Name: String = "default"
)