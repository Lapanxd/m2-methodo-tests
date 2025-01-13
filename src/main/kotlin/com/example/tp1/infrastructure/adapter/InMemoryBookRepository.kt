package com.example.tp1.infrastructure.adapter

import com.example.tp1.domain.model.Book
import com.example.tp1.domain.port.BookPort
import org.springframework.stereotype.Component

@Component
class InMemoryBookRepository : BookPort {
    private val books = mutableListOf<Book>()

    override fun add(book: Book) {
        books.add(book)
    }

    override fun findAll(): List<Book> {
        return books
    }
}