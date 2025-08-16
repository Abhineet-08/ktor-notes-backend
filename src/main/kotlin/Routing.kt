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
    db: UserRepo,
    jwtService: JwtService
) {
    val hashFunction = { s: String -> hash(s) }

    routing {
        // Temporarily disable these to find the conflict
         UserRoutes(db, jwtService, hashFunction)
         NoteRoutes(db, hashFunction)

        // This should be the ONLY active route
        get("/") {
            call.respondText("Hello from the isolated root route!")
        }
    }
}