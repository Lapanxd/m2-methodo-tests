import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.server.ResponseStatusException

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException::class)
    fun handleResponseStatusException(e: ResponseStatusException): ResponseEntity<String> {
        return ResponseEntity
            .status(e.statusCode)
            .body(e.reason ?: "Unexpected error") // Inclut le message dans le corps
    }
}