package com.example.repository

import com.example.data.table.NoteTable
import com.example.data.table.UserTable
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.config.ApplicationConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {

    fun init(config: ApplicationConfig) {
        // Read all the database properties from the configuration file
        val driverClassName = config.property("db.driver").getString()
        val originalJdbcURL = config.property("db.url").getString()
        val user = config.property("db.user").getString()
        val password = config.property("db.password").getString()

        // *** THIS IS THE FIX ***
        // Prepend "jdbc:" to the URL provided by Render to make it compatible with the Java JDBC driver.
        val correctedJdbcURL = "jdbc:${originalJdbcURL}"

        // Configure Hikari with the corrected URL and other credentials
        val hikariConfig = HikariConfig().apply {
            this.driverClassName = driverClassName
            this.jdbcUrl = correctedJdbcURL // Use the fixed URL
            this.username = user
            this.password = password
            maximumPoolSize = 3
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        }

        Database.connect(HikariDataSource(hikariConfig))

        // Create database tables
        transaction {
            SchemaUtils.create(UserTable)
            SchemaUtils.create(NoteTable)
        }
    }

    suspend fun <T> dbQuery(block: () -> T): T = withContext(Dispatchers.IO) {
        transaction { block() }
    }
}