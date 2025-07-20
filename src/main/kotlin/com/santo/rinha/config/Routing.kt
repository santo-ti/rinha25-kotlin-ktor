package com.santo.rinha.config

import com.santo.rinha.infra.routes.paymentRoutes
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        paymentRoutes(
            DependencyContainer.processPaymentPort,
            DependencyContainer.getPaymentSummaryPort
        )
    }
}
