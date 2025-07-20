package com.santo.rinha

import com.santo.rinha.config.DIConfig.configureDependencyInjection
import com.santo.rinha.config.DatabaseConfig.configureDatabase
import com.santo.rinha.config.SerializationConfig.configureSerialization
import com.santo.rinha.config.configureRouting
import io.ktor.server.application.Application
import io.ktor.server.netty.EngineMain

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    configureSerialization()
    configureDependencyInjection()
    configureDatabase()
    configureRouting()
}
