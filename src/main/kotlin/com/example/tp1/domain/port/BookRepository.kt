package com.example.tp1.domain.port

import com.example.tp1.domain.model.Book

interface BookRepository {
    fun add(book: Book)
    fun findAll(): List<Book>
}