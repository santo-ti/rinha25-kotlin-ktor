package app

import com.santo.rinha.config.DIConfig.configureDependencyInjection
import com.santo.rinha.config.DatabaseConfig.configureDatabase
import com.santo.rinha.config.SerializationConfig.configureSerialization
import com.santo.rinha.config.configureRouting
import io.ktor.server.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

fun main() {
    embeddedServer(Netty, port = 9999, module = Application::module).start(wait = true)
}

fun Application.module() {
    configureSerialization()
    configureDependencyInjection()
    configureDatabase()
    configureRouting()
}