package com.example.tp1

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.char
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll

class CaesarCipherTests : FunSpec({
    test("should return C") {
        // Given
        val char = 'A'
        val key = 2
        // When
        val res = cipher(char, key)

        // Then
        res shouldBe 'C'
    }

    test("should return F") {
        // Given
        val char = 'A'
        val key = 5

        // When
        val res = cipher(char, key)

        // Then
        res shouldBe 'F'
    }

    test("should restart to A after Z") {
        // Given
        val char = 'Z'
        val key = 2

        // When
        val res = cipher(char, key)

        // Then
        res shouldBe 'B'
    }

    test("should throw an error because char must be uppercase") {
        // Given
        val char = 'a'
        val key: Int = 2

        // When
        shouldThrow<IllegalArgumentException> {
            cipher(char, key)
        }.message shouldBe "Char must be uppercase"
    }

    test("should throw an error because key must be positive") {
        // Given
        val char = 'A'
        val key: Int = -1

        // Then
        shouldThrow<IllegalArgumentException> {
            cipher(char, key)
        }.message shouldBe "Key must be positive"
    }

    test("should return base char with a key of 0") {
        checkAll(Arb.char('A'..'Z')) { char ->
            // Given
            val key = 0

            // When
            val res = cipher(char, key)

            // Then
            res shouldBe char
        }
    }

    test("should produce the same result for chained and combined keys") {
        checkAll(Arb.char('A'..'Z'), Arb.int(0..25), Arb.int(0..25)) { char, key1, key2 ->
            // When
            val resultWithChainedKeys = cipher(cipher(char, key1), key2)
            val resultWithCombinedKey = cipher(char, key1 + key2)

            // Then
            resultWithChainedKeys shouldBe resultWithCombinedKey
        }
    }

    test("should return the same character with a key of 26") {
        checkAll(Arb.char('A'..'Z')) { char ->
            // Given
            val key = 26

            // When
            val res = cipher(char, key)

            // Then
            res shouldBe char
        }
    }
})