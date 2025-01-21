package com.example.tp1.infrastructure.driven.adapter

import com.example.tp1.domain.model.Book
import com.example.tp1.domain.port.BookPort
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BookDao(private val namedParameterJdbcTemplate: NamedParameterJdbcTemplate) : BookPort {
    override fun findAll(): List<Book> {
        return namedParameterJdbcTemplate
            .query("SELECT * FROM BOOK", MapSqlParameterSource()) { rs, _ ->
                Book(
                    title = rs.getString("title"),
                    author = rs.getString("author"),
                    isReserved = rs.getBoolean("is_reserved")

                )
            }
    }

    override fun add(book: Book) {
        namedParameterJdbcTemplate
            .update(
                "INSERT INTO BOOK (title, author, is_reserved) values (:title, :author, :is_reserved)", mapOf(
                    "title" to book.title,
                    "author" to book.author,
                    "is_reserved" to book.isReserved
                )
            )
    }

    @Transactional
    override fun reserveBook(book: Book) {
        val rowsUpdated = namedParameterJdbcTemplate.update(
            """
        UPDATE BOOK
        SET is_reserved = TRUE
        WHERE title = :title AND author = :author AND is_reserved = FALSE
        """.trimIndent(),
            mapOf(
                "title" to book.title,
                "author" to book.author
            )
        )

        println(rowsUpdated.toString())
        if (rowsUpdated == 0) {
            throw IllegalArgumentException("Book not found or already reserved")
        }
    }
}