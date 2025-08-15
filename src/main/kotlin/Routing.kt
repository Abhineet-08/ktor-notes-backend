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

fun Application.configureRouting() {
    val hashFunction = { s: String -> hash(s) }
    val jwtService = JwtService()
    val db = UserRepo()
    routing {

        UserRoutes(db, jwtService, hashFunction)
        NoteRoutes(db, hashFunction)

        get("/") {
            call.respondText("Hello World!")
        }
        get("/token") {
            val email = call.request.queryParameters["email"]
            val password = call.request.queryParameters["password"]
            val userName = call.request.queryParameters["userName"]

            val user = UserModel(email!!, hashFunction(password!!), userName!!)

            call.respond(jwtService.generateToken(user))
        }
    }
}
