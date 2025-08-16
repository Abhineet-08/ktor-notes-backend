package com.example.repository

import com.example.data.model.NoteModel
import com.example.data.model.UserModel
import com.example.data.table.NoteTable
import com.example.data.table.UserTable
import com.example.repository.DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.update

open class UserRepo {

    suspend fun addUser(user: UserModel) {
        dbQuery {
            UserTable.insert { ut ->
                ut[UserTable.email] = user.email
                ut[UserTable.password] = user.password
                ut[UserTable.userName] = user.userName
            }

        }
    }

    suspend fun searchUserByEmail(email: String) = dbQuery {
        UserTable.select { UserTable.email.eq(email) }
            .map { rowToUser(it) }
            .singleOrNull()

    }


    private fun rowToUser(row: ResultRow?): UserModel? {
        if (row == null) {
            return null
        }
        return UserModel(
            email = row[UserTable.email],
            password = row[UserTable.password],
            userName = row[UserTable.userName]
        )
    }


    suspend fun addNotes(note: NoteModel, email: String) {
        dbQuery {
            NoteTable.insert { nt ->
                nt[NoteTable.id] = note.id
                nt[NoteTable.userEmail] = email
                nt[NoteTable.noteTitle] = note.noteTitle
                nt[NoteTable.description] = note.description
                nt[NoteTable.date] = note.date
            }
        }
    }

    suspend fun updateNote(note: NoteModel, email: String) {
        dbQuery {
            NoteTable.update(
                where = {
                    NoteTable.userEmail.eq(email) and NoteTable.id.eq(note.id)
                }
            ) { nt ->
                nt[NoteTable.noteTitle] = note.noteTitle
                nt[NoteTable.description] = note.description
                nt[NoteTable.date] = note.date
            }
        }
    }

    suspend fun deleteNote(id: String, email: String) {
        dbQuery {
            NoteTable.deleteWhere {
                NoteTable.id.eq(id) and NoteTable.userEmail.eq(email)
            }
        }
    }

    suspend fun getAllNotes(email: String): List<NoteModel> = dbQuery {
        NoteTable.select { NoteTable.userEmail.eq(email) }
            .mapNotNull { rowToNote(it) }
    }


    private fun rowToNote(row: ResultRow?): NoteModel? {
        if (row == null) {
            return null
        }
        return NoteModel(
            id = row[NoteTable.id],
            noteTitle = row[NoteTable.noteTitle],
            description = row[NoteTable.description],
            date = row[NoteTable.date]
        )

    }


}