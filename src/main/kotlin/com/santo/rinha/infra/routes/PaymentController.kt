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
import io.ktor.server.routing.route
import kotlinx.serialization.Serializable

@Serializable
data class PaymentRequestDto(
    val value: Long,
    val description: String
)

@Serializable
data class PaymentResponseDto(
    val transaction_id: String,
    val status: String,
    val fee: Long
)

@Serializable
data class PaymentSummaryDto(
    val total_processed: Long,
    val total_failed: Long,
    val total_fee: Long,
    val total_value: Long
)

fun Route.paymentRoutes(
    processPaymentPort: ProcessPaymentPort,
    getPaymentSummaryPort: GetPaymentSummaryPort
) {
    route("/payments") {
        post {
            try {
                val request = call.receive<PaymentRequestDto>()

                // Basic validation
                if (request.value <= 0) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid value"))
                    return@post
                }

                if (request.description.isBlank()) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Description is required"))
                    return@post
                }

                val payment = processPaymentPort.processPayment(request.value, request.description)

                val response = PaymentResponseDto(
                    transaction_id = payment.transactionId,
                    status = payment.status.name.lowercase(),
                    fee = payment.fee
                )

                call.respond(HttpStatusCode.OK, response)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Internal server error"))
            }
        }
    }

    get("/payments-summary") {
        try {
            val summary = getPaymentSummaryPort.getPaymentSummary()

            val response = PaymentSummaryDto(
                total_processed = summary.totalProcessed,
                total_failed = summary.totalFailed,
                total_fee = summary.totalFee,
                total_value = summary.totalValue
            )

            call.respond(HttpStatusCode.OK, response)
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Internal server error"))
        }
    }
}
