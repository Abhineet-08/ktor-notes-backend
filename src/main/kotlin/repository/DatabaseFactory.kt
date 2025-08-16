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
import java.net.URI

object DatabaseFactory {

    fun init(config: ApplicationConfig) {
        // Read the main database URL from the configuration file
        val originalJdbcURL = config.property("db.url").getString()

        // Configure Hikari with the appropriate credentials
        val hikariConfig = createHikariConfig(originalJdbcURL, config)
        Database.connect(HikariDataSource(hikariConfig))

        // Create database tables
        transaction {
            SchemaUtils.create(UserTable)
            SchemaUtils.create(NoteTable)
        }
    }

    private fun createHikariConfig(dbUrl: String, appConfig: ApplicationConfig): HikariConfig {
        val config = HikariConfig()

        // Check if we are using a local URL or a Render-style URL
        if (dbUrl.startsWith("jdbc:postgresql://")) {
            // It's a local URL, use it directly and get credentials from config
            config.jdbcUrl = dbUrl
            config.username = appConfig.property("db.user").getString()
            config.password = appConfig.property("db.password").getString()
        } else {
            // It's a Render-style URL (e.g., "postgresql://..."), parse it
            val dbUri = URI(dbUrl)
            val userInfo = dbUri.userInfo.split(":")
            val username = userInfo[0]
            val password = userInfo[1]
            val host = dbUri.host
            // *** THIS IS THE FIX ***
            // Use the default PostgreSQL port (5432) if the URL doesn't specify one
            val port = if (dbUri.port == -1) 5432 else dbUri.port
            val path = dbUri.path

            // Reconstruct the URL into the format the Java JDBC driver expects
            config.jdbcUrl = "jdbc:postgresql://$host:$port$path"
            config.username = username
            config.password = password
        }

        config.driverClassName = "org.postgresql.Driver"
        config.maximumPoolSize = 3
        config.isAutoCommit = false
        config.transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        config.validate()
        return config
    }

    suspend fun <T> dbQuery(block: () -> T): T = withContext(Dispatchers.IO) {
        transaction { block() }
    }
}
