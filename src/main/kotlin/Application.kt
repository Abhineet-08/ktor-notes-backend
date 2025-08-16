package com.example

import com.example.authentication.JwtService
import com.example.authentication.hash
import com.example.repository.DatabaseFactory
import com.example.repository.UserRepo
import io.ktor.locations.Locations
import io.ktor.server.application.*
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.jwt.jwt

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

// In Application.kt

fun Application.module() {
    // 1. Initialize the database
    DatabaseFactory.init(environment.config)

    // 2. Create your dependencies using the main application config
    val db = UserRepo()
    val jwtService = JwtService(environment.config)
    // Read the secret key from the config file
    val hashSecret = environment.config.property("hash.secret").getString()

    // Create a hash function that uses the secret
    val hashFunction = { s: String -> hash(s, hashSecret) }


    // 3. Install Authentication and pass the dependencies to it
    install(Authentication) {
        jwt("jwt") {
            verifier(jwtService.verifier)
            realm = "Note Server"
            validate {
                val payload = it.payload
                val email = payload.getClaim("email").asString()
                val user = db.searchUserByEmail(email)
                user
            }
        }
    }

    // 4. Pass dependencies to your other configuration functions
    configureSerialization()
    // configureSecurity() // You might need to update this function as well if it uses db or jwtService
    configureRouting(db, jwtService , hashFunction)
}
