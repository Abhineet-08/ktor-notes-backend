package com.example

import com.example.authentication.JwtService
import com.example.authentication.hash
import com.example.data.model.UserModel
import com.example.repository.UserRepo
import com.example.routes.NoteRoutes
import com.example.routes.UserRoutes
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

// In your configureRouting file

fun Application.configureRouting(
    db: UserRepo, // Accept UserRepo as a parameter
    jwtService: JwtService // Accept JwtService as a parameter
) {
    val hashFunction = { s: String -> hash(s) }
    // The function no longer creates its own dependencies
    // val jwtService = JwtService() // DELETE
    // val db = UserRepo() // DELETE

    routing {
        UserRoutes(db, jwtService, hashFunction)
        NoteRoutes(db, hashFunction)

        get("/") {
            call.respondText("Hello World!")
        }
        // ... your other routes
    }
}