package com.example.tp1.infrastructure.application

import com.example.tp1.domain.port.BookPort
import com.example.tp1.domain.usecase.BookManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class BookUseCasesConfiguration {

    @Bean
    fun bookUseCases(bookPort: BookPort): BookManager {
        return BookManager(bookPort)
    }
}