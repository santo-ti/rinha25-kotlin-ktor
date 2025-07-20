package com.santo.rinha.domain.ports

import com.santo.rinha.domain.Payment
import com.santo.rinha.domain.PaymentSummary
import java.math.BigDecimal
import java.util.UUID

interface ProcessPaymentPort {
    suspend fun processPayment(correlationId: UUID, amount: BigDecimal)
}

interface GetPaymentSummaryPort {
    suspend fun getPaymentSummary(from: String?, to: String?): PaymentSummary
}

interface PaymentRepositoryPort {
    suspend fun save(payment: Payment)
    suspend fun getSummary(from: String?, to: String?): PaymentSummary
}

interface PaymentProcessorPort {
    suspend fun processPayment(correlationId: UUID, amount: BigDecimal)
}

