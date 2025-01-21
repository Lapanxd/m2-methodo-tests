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

class BookUseCaseTests : FunSpec({
    val bookRepositoryMock = mockk<BookPort>()
    val bookUseCase = BookUseCase(bookRepositoryMock)

    beforeTest {
        clearMocks(bookRepositoryMock)
    }

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

    test("should reserve a book if it is not already reserved") {
        // Given
        val book = Book("Kotlin Basics", "John Doe", isReserved = false)
        every { bookRepositoryMock.findAll() } returns listOf(book)
        every { bookRepositoryMock.reserveBook(book) } just Runs

        // When
        bookUseCase.reserveBook("Kotlin Basics")

        // Then
        verify { bookRepositoryMock.reserveBook(book) }
    }

    test("should throw an error if the book is already reserved") {
        // Given
        val reservedBook = Book("Kotlin Advanced", "Jane Doe", isReserved = true)
        val otherBook = Book("Kotlin Basics", "John Doe", isReserved = false)
        every { bookRepositoryMock.findAll() } returns listOf(reservedBook, otherBook)

        // When & Then
        val exception = shouldThrow<IllegalStateException> {
            bookUseCase.reserveBook("Kotlin Advanced")
        }
        exception.message shouldBe "Book with title 'Kotlin Advanced' is already reserved"

        // Verify that reserveBook is not called
        verify(exactly = 0) { bookRepositoryMock.reserveBook(any()) }
    }

    test("should throw an error if the book does not exist") {
        // Given
        every { bookRepositoryMock.findAll() } returns emptyList()

        // When & Then
        val exception = shouldThrow<IllegalArgumentException> {
            bookUseCase.reserveBook("Nonexistent Book")
        }
        exception.message shouldBe "Book with title 'Nonexistent Book' not found"
        verify(exactly = 0) { bookRepositoryMock.reserveBook(any()) }
    }
})