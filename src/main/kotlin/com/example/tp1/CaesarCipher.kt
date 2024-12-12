package com.example.tp1

fun cipher(char: Char, key: Int): Char {
    if (char.isLowerCase()) throw IllegalArgumentException("Char must be uppercase")
    if (key < 0) throw IllegalArgumentException("Key must be positive")
    if (char in 'A'..'Z') {
        return ((char - 'A' + key) % 26 + 'A'.code).toChar()
    }
    return char
}