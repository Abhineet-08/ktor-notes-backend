package com.example.authentication

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.example.data.model.UserModel
import io.ktor.server.config.ApplicationConfig

class JwtService(config: ApplicationConfig) {

    private val issuer = "noteServer"
    private val jwtSecret = config.property("jwt.secret").getString()
    private val algorithm = Algorithm.HMAC512(jwtSecret)
    val verifier: JWTVerifier = JWT
        .require(algorithm)
        .withIssuer(issuer)
        .build()


    fun generateToken(user: UserModel): String {
        return JWT.create()
            .withSubject("NOteAuthentication")
            .withIssuer(issuer)
            .withClaim("email", user.email)
            .sign(algorithm)
    }

}