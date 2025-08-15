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

fun Application.module() {

    DatabaseFactory.init()
    val db = UserRepo()
    val jwtService = JwtService()
    val hashFunction = { s: String -> hash(s) }

    install(Authentication){
        jwt("jwt"){
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

    configureSerialization()
    configureSecurity()
    configureRouting()
}
