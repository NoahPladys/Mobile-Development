package edu.app.sqlite

data class Todo(
    var id: Int,
    var userId: Int,
    var title: String,
    var completed: Int
)