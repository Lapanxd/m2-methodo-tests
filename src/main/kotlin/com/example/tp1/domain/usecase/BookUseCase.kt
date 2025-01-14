package com.example.tp1.domain.usecase

import com.example.tp1.domain.model.Book
import com.example.tp1.domain.port.BookPort

class BookUseCase(private val bookRepository: BookPort) {

    fun createBook(title: String, author: String) {
        val book = Book(title, author)
        bookRepository.add(book)
    }

    fun listBooks(): List<Book> {
        return bookRepository.findAll().sortedBy { it.title }
    }
}