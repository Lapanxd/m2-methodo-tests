package com.example.tp1.domain.model

data class Book(
    val title: String,
    val author: String,
    val isReserved: Boolean = false,
) {
    init {
        require(title.isNotBlank()) { "Title cannot be blank" }
        require(author.isNotBlank()) { "Author cannot be blank" }
    }
}