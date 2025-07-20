package com.santo.rinha.infra.repositories

import com.santo.rinha.domain.ports.PaymentProcessorPort
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.math.BigDecimal
import java.util.UUID

@Serializable
data class PaymentProcessorRequest(
    val correlationId: UUID,
    val amount: BigDecimal
)

class HttpPaymentProcessor(
    private val baseUrl: String,
    private val timeoutMs: Long = 5000L
) : PaymentProcessorPort {

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
        install(HttpTimeout) {
            requestTimeoutMillis = timeoutMs
            connectTimeoutMillis = 2000L
            socketTimeoutMillis = timeoutMs
        }
    }

    override suspend fun processPayment(correlationId: UUID, amount: BigDecimal) {
        val response = client.post("$baseUrl/payments") {
            contentType(ContentType.Application.Json)
            setBody(PaymentProcessorRequest(correlationId, amount))
        }
        if (!response.status.isSuccess()) {
            throw RuntimeException("Payment processor error: ${'$'}{response.status}")
        }
    }

    fun close() {
        client.close()
    }
}

