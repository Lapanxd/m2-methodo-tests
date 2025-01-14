import com.example.tp1.Tp1Application
import com.example.tp1.domain.model.Book
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.testcontainers.containers.PostgreSQLContainer

@SpringBootTest(classes = [Tp1Application::class])
@ActiveProfiles("testIntegration")
class BookDaoIntegrationTests(
    @Autowired private val bookDao: BookDao
) : FunSpec() {

    init {
        extension(SpringExtension)

        beforeTest {
            performQuery("DELETE FROM book")
        }

        afterSpec {
            container.stop()
        }

        test("findAll should return an empty list when no books exist") {
            val books = bookDao.findAll()
            books.shouldBe(emptyList())
        }

        test("add should insert a new book into the database") {
            val book = Book(title = "1984", author = "George Orwell")
            shouldNotThrowAny {
                bookDao.add(book)
            }

            val books = bookDao.findAll()
            books.shouldContainExactly(book)
        }

        test("add should insert multiple books and findAll should retrieve them") {
            val book1 = Book(title = "1984", author = "George Orwell")
            val book2 = Book(title = "Brave New World", author = "Aldous Huxley")

            shouldNotThrowAny {
                bookDao.add(book1)
                bookDao.add(book2)
            }

            val books = bookDao.findAll()
            books.shouldContainExactly(book1, book2)
        }
    }

    companion object {
        private val container = PostgreSQLContainer<Nothing>("postgres:13-alpine")

        init {
            container.start()
            System.setProperty("spring.datasource.url", container.jdbcUrl)
            System.setProperty("spring.datasource.username", container.username)
            System.setProperty("spring.datasource.password", container.password)
        }
    }

    private fun performQuery(sql: String) {
        container.createConnection("").use { connection ->
            connection.createStatement().use { statement ->
                statement.execute(sql)
            }
        }
    }
}