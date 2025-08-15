package com.example.routes

import com.example.data.model.NoteModel
import com.example.data.model.SimpleResponse
import com.example.data.model.UserModel
import com.example.repository.UserRepo
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post

const val NOTES = "$API_VERSION/notes"
const val CREATE_NOTES = "$NOTES/create"
const val UPDATE_NOTES = "$NOTES/update"
const val DELETE_NOTES = "$NOTES/delete"

fun Route.NoteRoutes(
    db: UserRepo,
    hashFunction: (String) -> String
) {
    authenticate("jwt") {

        // Create Note
        post(CREATE_NOTES) {
            val note = try {
                call.receive<NoteModel>()
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    SimpleResponse(false, "Missing Fields")
                )
                return@post
            }

            try {
                val email = call.principal<UserModel>()!!.email
                db.addNotes(note, email)
                call.respond(HttpStatusCode.OK, SimpleResponse(true, "Note Added Successfully!"))
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.Conflict,
                    SimpleResponse(false, e.message ?: "Some Problem Occurred!")
                )
            }
        }

        // Get All Notes
        get(NOTES) {
            try {
                val email = call.principal<UserModel>()!!.email
                val notes = db.getAllNotes(email)
                call.respond(HttpStatusCode.OK, notes)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.Conflict, emptyList<NoteModel>())
            }
        }

        // Update Note
        post(UPDATE_NOTES) {
            val note = try {
                call.receive<NoteModel>()
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    SimpleResponse(false, "Missing Fields")
                )
                return@post
            }

            try {
                val email = call.principal<UserModel>()!!.email
                db.updateNote(note, email)
                call.respond(HttpStatusCode.OK, SimpleResponse(true, "Note Updated Successfully!"))
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.Conflict,
                    SimpleResponse(false, e.message ?: "Some Problem Occurred!")
                )
            }
        }

        // Delete Note
        delete(DELETE_NOTES) {
            val noteId = call.request.queryParameters["id"]
            if (noteId.isNullOrBlank()) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    SimpleResponse(false, "QueryParameter: id is not present")
                )
                return@delete
            }

            try {
                val email = call.principal<UserModel>()!!.email
                db.deleteNote(noteId, email)
                call.respond(HttpStatusCode.OK, SimpleResponse(true, "Note Deleted Successfully!"))
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.Conflict,
                    SimpleResponse(false, e.message ?: "Some Problem Occurred!")
                )
            }
        }
    }
}
