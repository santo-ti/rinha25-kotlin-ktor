package com.santo.rinha.domain.ports

import com.santo.rinha.domain.Payment
import com.santo.rinha.domain.PaymentSummary

// Driving Ports (Use Cases)
interface ProcessPaymentPort {
    suspend fun processPayment(value: Long, description: String): Payment
}

interface GetPaymentSummaryPort {
    suspend fun getPaymentSummary(): PaymentSummary
}

// Driven Ports (Infrastructure)
interface PaymentRepositoryPort {
    suspend fun save(payment: Payment): Payment
    suspend fun getSummary(): PaymentSummary
    suspend fun batchSave(payments: List<Payment>): List<Payment>
}

interface PaymentProcessorPort {
    suspend fun processPayment(value: Long, description: String): Payment
    suspend fun getServiceHealth(): ServiceHealth
}

data class ServiceHealth(
    val status: HealthStatus,
    val minResponseTimeMs: Long
)

enum class HealthStatus {
    OK,
    DEGRADED,
    UNAVAILABLE
}

