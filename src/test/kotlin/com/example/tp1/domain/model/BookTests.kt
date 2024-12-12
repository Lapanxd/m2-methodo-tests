package com.example.tp1.domain.model

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class BookTests : FunSpec({
    test("should create a book with valid properties") {
        // Given
        val title = "Title"
        val author = "Author"

        // When
        val book = Book(title, author)

        // Then
        book.title shouldBe title
        book.author shouldBe author
    }

    test("should throw an error if title is missing") {
        // Given
        val author = "Author"

        // When
        val exception = shouldThrow<IllegalArgumentException> {
            Book(title = "", author)
        }

        // Then
        exception.message shouldBe "Title cannot be blank"
    }

    test("should throw an error if author is missing") {
        // Given
        val title = "Title"

        // When
        val exception = shouldThrow<IllegalArgumentException> {
            Book(title, author = "")
        }

        // Then
        exception.message shouldBe "Author cannot be blank"
    }
})
