package com.example.tp1

import io.cucumber.java.Before
import io.cucumber.java.Scenario
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import io.kotest.matchers.shouldBe
import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import io.restassured.path.json.JsonPath
import io.restassured.response.ValidatableResponse
import org.springframework.boot.test.web.server.LocalServerPort

data class Book(val name: String, val author: String)

class BookStepDefs {
    @LocalServerPort
    private var port: Int? = 0

    @Before
    fun setup(scenario: Scenario) {
        RestAssured.baseURI = "http://localhost:$port"
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()
    }

    @When("the user adds the book {string} authored by {string}")
    fun createBook(title: String, author: String) {
        given()
            .contentType(ContentType.JSON)
            .body("""{"name":"$title","author":"$author"}""")
            .post("/books")
            .then()
            .statusCode(201)
    }

    @When("the user gets all books")
    fun getAllBooks() {
        lastBookResult = given().get("/books").then().statusCode(200)
    }

    @Then("the list should contain the following books in the same order")
    fun shouldHaveListOfBooks(payload: List<Map<String, Any>>) {
        val expectedResponse = payload.joinToString(",", "[", "]") { book ->
            book.entries.joinToString(",", "{", "}") { """"${it.key}":"${it.value}"""" }
        }
        lastBookResult.extract().body().jsonPath().prettify() shouldBe JsonPath(expectedResponse).prettify()
    }

    companion object {
        lateinit var lastBookResult: ValidatableResponse
    }
}