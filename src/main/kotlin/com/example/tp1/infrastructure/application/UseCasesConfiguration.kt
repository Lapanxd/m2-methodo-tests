package com.example.tp1.infrastructure.application

import com.example.tp1.domain.usecase.BookUseCase
import com.example.tp1.infrastructure.driven.adapter.BookDao
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class UseCasesConfiguration {

    @Bean
    fun bookUseCases(bookDao: BookDao): BookUseCase {
        return BookUseCase(bookDao)
    }
}