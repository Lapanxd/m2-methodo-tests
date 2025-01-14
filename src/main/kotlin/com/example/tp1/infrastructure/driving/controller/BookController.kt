package com.example.tp1.infrastructure.driving.controller

import com.example.tp1.domain.usecase.BookUseCase
import com.example.tp1.infrastructure.driving.controller.dto.BookDto
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/books")
class BookController(private val bookUseCase: BookUseCase) {

    @GetMapping
    fun getBooks(): List<BookDto> {
        return try {
            bookUseCase.listBooks().map { book -> BookDto(book.title, book.author) }
        } catch (e: Exception) {
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to fetch books", e)
        }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createBook(@RequestBody bookDto: BookDto): BookDto {
        return try {
            bookUseCase.createBook(bookDto.title, bookDto.author)
            bookDto
        } catch (e: IllegalArgumentException) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "Book already exists", e)
        } catch (e: Exception) {
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to create book", e)
        }
    }
}