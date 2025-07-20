package com.santo.rinha.infra.repositories

import com.santo.rinha.domain.Payment
import com.santo.rinha.domain.PaymentStatus
import com.santo.rinha.domain.ports.HealthStatus
import com.santo.rinha.domain.ports.PaymentProcessorPort
import com.santo.rinha.domain.ports.ServiceHealth
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class PaymentRequest(
    val value: Long,
    val description: String
)

@Serializable
data class PaymentResponse(
    val transaction_id: String,
    val status: String,
    val fee: Long = 0L
)

@Serializable
data class HealthResponse(
    val status: String,
    val min_response_time_ms: Long
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

    override suspend fun processPayment(value: Long, description: String): Payment {
        return try {
            val response = client.post("$baseUrl/payments") {
                contentType(ContentType.Application.Json)
                setBody(PaymentRequest(value, description))
            }

            if (response.status.isSuccess()) {
                val paymentResponse = response.body<PaymentResponse>()
                Payment(
                    transactionId = paymentResponse.transaction_id,
                    value = value,
                    description = description,
                    status = if (paymentResponse.status == "processed") PaymentStatus.PROCESSED else PaymentStatus.FAILED,
                    fee = paymentResponse.fee
                )
            } else {
                Payment(
                    value = value,
                    description = description,
                    status = PaymentStatus.FAILED,
                    fee = 0L
                )
            }
        } catch (e: TimeoutCancellationException) {
            Payment(
                value = value,
                description = description,
                status = PaymentStatus.FAILED,
                fee = 0L
            )
        } catch (e: Exception) {
            Payment(
                value = value,
                description = description,
                status = PaymentStatus.FAILED,
                fee = 0L
            )
        }
    }

    override suspend fun getServiceHealth(): ServiceHealth {
        return try {
            val response = client.get("$baseUrl/payments/service-health")

            if (response.status.isSuccess()) {
                val healthResponse = response.body<HealthResponse>()
                ServiceHealth(
                    status = when (healthResponse.status) {
                        "ok" -> HealthStatus.OK
                        "degraded" -> HealthStatus.DEGRADED
                        else -> HealthStatus.UNAVAILABLE
                    },
                    minResponseTimeMs = healthResponse.min_response_time_ms
                )
            } else {
                ServiceHealth(HealthStatus.UNAVAILABLE, 0L)
            }
        } catch (e: Exception) {
            ServiceHealth(HealthStatus.UNAVAILABLE, 0L)
        }
    }

    fun close() {
        client.close()
    }
}

