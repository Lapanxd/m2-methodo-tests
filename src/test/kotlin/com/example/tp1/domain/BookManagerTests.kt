package com.example.tp1.domain

import com.example.tp1.domain.model.Book
import com.example.tp1.domain.port.BookPort
import com.example.tp1.domain.usecase.BookUseCase
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.filter
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import io.mockk.*

class BookManagerTests : FunSpec({
    val bookRepositoryMock = mockk<BookPort>()
    val bookUseCase = BookUseCase(bookRepositoryMock)

    test("should add a book to the repository") {
        // Given
        every { bookRepositoryMock.add(any()) } just Runs

        // When
        bookUseCase.createBook("Mon super livre", "Moi")

        // Then
        verify { bookRepositoryMock.add(Book("Mon super livre", "Moi")) }
    }

    test("should return all books from the repository") {
        // Given
        val books = listOf(
            Book("C Livre", "Morgan"),
            Book("A Livre", "Pas morgan"),
            Book("B Livre", "Quelqu'un"),
        )
        every { bookRepositoryMock.findAll() } returns books

        // When
        val result = bookUseCase.listBooks()


        // Then
        result shouldBe listOf(
            Book("A Livre", "Pas morgan"),
            Book("B Livre", "Quelqu'un"),
            Book("C Livre", "Morgan"),
        )
        verify { bookRepositoryMock.findAll() }
    }

    test("should return an empty list if repository is empty") {
        // Given
        every { bookRepositoryMock.findAll() } returns emptyList()

        // When
        val result = bookUseCase.listBooks()

        // Then
        result shouldBe emptyList()
        verify { bookRepositoryMock.findAll() }
    }

    test("all added books should be listed & sorted by title") {
        // Given
        val booksAdded = mutableListOf<Book>()
        val nonBlankStringArb = Arb.string(minSize = 1).filter { it.isNotBlank() }
        every { bookRepositoryMock.add(any()) } answers { booksAdded.add(firstArg()) }
        every { bookRepositoryMock.findAll() } answers { booksAdded }

        checkAll(nonBlankStringArb, nonBlankStringArb) { title, author ->
            // When
            val book = Book(title, author)
            bookUseCase.createBook(title, author)

            // Then
            verify { bookRepositoryMock.add(book) }
            val sortedBooks = booksAdded.sortedBy { it.title }
            bookUseCase.listBooks() shouldBe sortedBooks
        }
    }

    test("should not allow books with empty title or author") {
        // Given
        val validAuthor = "Valid Author"
        val validTitle = "Valid Title"
        val emptyString = ""

        // When & Then
        shouldThrow<IllegalArgumentException> { bookUseCase.createBook(emptyString, validAuthor) }
            .message shouldBe "Title cannot be blank"
        shouldThrow<IllegalArgumentException> { bookUseCase.createBook(validTitle, emptyString) }
            .message shouldBe "Author cannot be blank"
    }
})