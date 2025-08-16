// In ApplicationTest.kt

import com.example.authentication.JwtService
import com.example.configureRouting
import com.example.repository.UserRepo
import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsText
import io.ktor.http.*
import io.ktor.serialization.gson.gson
import io.ktor.server.application.*
import io.ktor.server.auth.* // <-- Import Authentication
import io.ktor.server.auth.jwt.* // <-- Import JWT
import io.ktor.server.config.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.testing.*
import kotlin.test.*

class ApplicationTest {

    // A fake repository that does nothing, which is fine for this test
    class FakeUserRepo : UserRepo()

    @Test
    fun testRoot() = testApplication {
        val testConfig = ApplicationConfig("application-test.yaml")
        val jwtService = JwtService(testConfig)

        application {
            // Install ALL necessary plugins for the code under test
            install(ContentNegotiation) {
                gson()
            }
            install(Authentication) {
                // Set up a dummy JWT authenticator to match the main app
                jwt("jwt") {
                    verifier(jwtService.verifier)
                    realm = "Test Server"
                    validate {
                        // For the "/" test, this block is never actually run,
                        // so we can just return null.
                        null
                    }
                }
            }

            // Now, provide the dependencies to your routing function
            configureRouting(FakeUserRepo(), jwtService)
        }

        client.get("/").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("Hello World!", bodyAsText())
        }
    }
}