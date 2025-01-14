package com.example.tp1.infrastructure.driven.adapter

import com.example.tp1.domain.model.Book
import com.example.tp1.domain.port.BookPort
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Service

@Service
class BookDao(private val namedParameterJdbcTemplate: NamedParameterJdbcTemplate) : BookPort {
    override fun findAll(): List<Book> {
        return namedParameterJdbcTemplate
            .query("SELECT * FROM BOOK", MapSqlParameterSource()) { rs, _ ->
                Book(
                    title = rs.getString("title"),
                    author = rs.getString("author")
                )
            }
    }

    override fun add(book: Book) {
        namedParameterJdbcTemplate
            .update(
                "INSERT INTO BOOK (title, author) values (:title, :author)", mapOf(
                    "title" to book.title,
                    "author" to book.author
                )
            )
    }
}