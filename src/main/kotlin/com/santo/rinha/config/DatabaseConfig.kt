package com.santo.rinha.config

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.Application
import org.jetbrains.exposed.sql.Database
import javax.sql.DataSource

object DatabaseConfig {
    fun Application.configureDatabase() {
        val dataSource = createDataSource()
        Database.connect(dataSource)
    }

    private fun createDataSource(): DataSource {
        val config = HikariConfig().apply {
            jdbcUrl = System.getenv("DATABASE_URL") ?: "jdbc:postgresql://localhost:5432/rinha"
            username = System.getenv("DATABASE_USER") ?: "postgres"
            password = System.getenv("DATABASE_PASSWORD") ?: "postgres"
            driverClassName = "org.postgresql.Driver"

            // Optimized connection pool settings for limited resources
            maximumPoolSize = 2
            minimumIdle = 1
            connectionTimeout = 3000
            idleTimeout = 300000
            maxLifetime = 600000
            leakDetectionThreshold = 60000

            // Performance optimizations
            addDataSourceProperty("cachePrepStmts", "true")
            addDataSourceProperty("prepStmtCacheSize", "250")
            addDataSourceProperty("prepStmtCacheSqlLimit", "2048")
            addDataSourceProperty("useServerPrepStmts", "true")
            addDataSourceProperty("rewriteBatchedStatements", "true")
        }

        return HikariDataSource(config)
    }
}
