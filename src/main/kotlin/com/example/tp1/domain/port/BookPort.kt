package com.example.tp1.domain.port

import com.example.tp1.domain.model.Book
import org.springframework.stereotype.Repository

@Repository
interface BookPort {
    fun add(book: Book)
    fun findAll(): List<Book>
}