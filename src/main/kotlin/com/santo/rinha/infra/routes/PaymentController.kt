package com.santo.rinha.infra.routes

import com.santo.rinha.domain.ports.GetPaymentSummaryPort
import com.santo.rinha.domain.ports.ProcessPaymentPort
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import kotlinx.serialization.Serializable
import java.math.BigDecimal
import java.util.UUID

@Serializable
data class PaymentRequestDto(
    val correlationId: UUID,
    val amount: BigDecimal
)

fun Route.paymentRoutes(
    processPaymentPort: ProcessPaymentPort,
    getPaymentSummaryPort: GetPaymentSummaryPort
) {
    post("/payments") {
        try {
            val request = call.receive<PaymentRequestDto>()
            // Validação básica
            if (request.amount <= BigDecimal.ZERO) {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }
            processPaymentPort.processPayment(request.correlationId, request.amount)
            call.respond(HttpStatusCode.Accepted)
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError)
        }
    }

    get("/payments-summary") {
        try {
            val from = call.request.queryParameters["from"]
            val to = call.request.queryParameters["to"]
            val summary = getPaymentSummaryPort.getPaymentSummary(from, to)
            call.respond(HttpStatusCode.OK, summary)
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError)
        }
    }
}
