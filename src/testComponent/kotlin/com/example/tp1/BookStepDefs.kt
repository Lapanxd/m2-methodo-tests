package com.example.tp1

import io.cucumber.java.en.And
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import org.assertj.core.api.Assertions.assertThat

data class Book(val name: String, val author: String)

class BookStepDefs {

    private val books = mutableListOf<Book>()

    @When("the user adds the book {string} authored by {string}")
    fun theUserAddsTheBook(name: String, author: String) {
        books.add(Book(name, author))
    }

    @And("the user retrieves all books")
    fun theUserRetrievesAllBooks() {
        // Retrieval is implicit since we're directly using the `books` list
    }

    @Then("the list should include the following books in the same sequence")
    fun theListShouldIncludeTheFollowingBooks(dataTable: List<Map<String, String>>) {
        val expectedBooks = dataTable.map { row -> Book(row["name"]!!, row["author"]!!) }
        assertThat(books).containsExactlyElementsOf(expectedBooks)
    }
}