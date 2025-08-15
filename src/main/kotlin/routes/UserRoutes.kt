package com.example.routes

import com.example.authentication.JwtService
import com.example.data.model.LoginRequest
import com.example.data.model.RegisterRequest
import com.example.data.model.SimpleResponse
import com.example.data.model.UserModel
import com.example.repository.UserRepo
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post


const val API_VERSION = "/v1"
const val USERS = "$API_VERSION/users"
const val REGISTER_REQUEST = "$USERS/register"
const val LOGIN_REQUEST = "$USERS/login"


class UserRegisterRoute

class UserLoginRoute


fun Route.UserRoutes(
    db: UserRepo,
    jwtService: JwtService,
    hashFunction: (String) -> String
) {

    // Register route
    post(REGISTER_REQUEST) {
        val registerRequest = try {
            call.receive<RegisterRequest>()
        } catch (e: Exception) {
            call.respond(HttpStatusCode.BadRequest, SimpleResponse(false, "Missing Some Fields"))
            return@post
        }

        try {
            val user = UserModel(
                registerRequest.email,
                hashFunction(registerRequest.password),
                registerRequest.userName
            )
            db.addUser(user)
            call.respond(HttpStatusCode.OK, SimpleResponse(true, jwtService.generateToken(user)))
        } catch (e: Exception) {
            call.respond(
                HttpStatusCode.Conflict,
                SimpleResponse(false, e.message ?: "Some Problem Occurred")
            )
        }
    }

    post(LOGIN_REQUEST) {
        val loginRequest = try {
            call.receive<LoginRequest>()
        } catch (e: Exception) {
            call.respond(HttpStatusCode.BadRequest, SimpleResponse(false, "Missing Some Fields"))
            return@post
        }

        try {
            val user = db.searchUserByEmail(loginRequest.email)
            if (user == null) {
                call.respond(HttpStatusCode.BadRequest, SimpleResponse(false, "Wrong Email Id"))
            } else {
                if (user.password == hashFunction(loginRequest.password)) {
                    call.respond(
                        HttpStatusCode.OK,
                        SimpleResponse(true, jwtService.generateToken(user))
                    )
                } else {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        SimpleResponse(false, "Password Incorrect")
                    )
                }
            }
        } catch (e: Exception) {
            call.respond(
                HttpStatusCode.Conflict,
                SimpleResponse(false, e.message ?: "Some Problem Occurred")
            )
        }
    }


}
