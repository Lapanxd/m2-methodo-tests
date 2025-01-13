package com.example.tp1.infrastructure.driving.controller

import com.example.tp1.domain.usecase.BookManager
import com.example.tp1.infrastructure.driving.controller.dto.BookDto
import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import io.mockk.every
import io.mockk.verify
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post

@WebMvcTest(BookController::class)
class BookControllerIntegrationTest(
    private var mockMvc: MockMvc, @MockkBean
    private var bookManager: BookManager
) : FunSpec({

    extension(SpringExtension)

    val objectMapper = ObjectMapper()

    test("GET /books should return a list of books (200)") {
        every { bookManager.listBooks() } returns listOf(
            com.example.tp1.domain.model.Book("Title1", "Author1"),
            com.example.tp1.domain.model.Book("Title2", "Author2")
        )

        mockMvc.get("/books")
            .andExpect {
                status { isOk() }
                content {
                    json(
                        """
                        [
                          {"title": "Title1", "author": "Author1"},
                          {"title": "Title2", "author": "Author2"}
                        ]
                        """.trimIndent()
                    )
                }
            }

        verify(exactly = 1) { bookManager.listBooks() }
    }

    test("POST /books should create a new book (201)") {
        val bookDto = BookDto("New Book", "New Author")
        every { bookManager.createBook(bookDto.title, bookDto.author) } returns Unit

        mockMvc.post("/books") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(bookDto)
        }
            .andExpect {
                status { isCreated() }
                content { json(objectMapper.writeValueAsString(bookDto)) }
            }

        verify(exactly = 1) { bookManager.createBook("New Book", "New Author") }
    }

    test("POST /books with invalid data should return 400 Bad Request") {
        val invalidJson = """
            {
              "title": "Missing Author"
            }
        """.trimIndent()

        mockMvc.post("/books") {
            contentType = MediaType.APPLICATION_JSON
            content = invalidJson
        }
            .andExpect {
                status { isBadRequest() }
            }

        verify(exactly = 0) { bookManager.createBook(any(), any()) }
    }

    test("GET /books should return 500 if domain throws exception") {
        every { bookManager.listBooks() } throws RuntimeException("Database error")

        mockMvc.get("/books")
            .andExpect {
                status { isInternalServerError() }
            }

        verify(exactly = 1) { bookManager.listBooks() }
    }

    test("POST /books should return 409 if book already exists") {
        val bookDto = BookDto("Existing Book", "Author")
        every {
            bookManager.createBook(
                bookDto.title,
                bookDto.author
            )
        } throws IllegalArgumentException("Book already exists")

        mockMvc.post("/books") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(bookDto)
        }
            .andExpect {
                status { isConflict() }
            }

        verify(exactly = 1) { bookManager.createBook("Existing Book", "Author") }
    }
})