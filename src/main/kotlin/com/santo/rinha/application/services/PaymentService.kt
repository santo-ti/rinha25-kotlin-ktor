package com.santo.rinha.application.services

import com.santo.rinha.domain.Payment
import com.santo.rinha.domain.PaymentSummary
import com.santo.rinha.domain.ports.GetPaymentSummaryPort
import com.santo.rinha.domain.ports.PaymentProcessorPort
import com.santo.rinha.domain.ports.PaymentRepositoryPort
import com.santo.rinha.domain.ports.ProcessPaymentPort
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

class PaymentService(
    private val paymentRepository: PaymentRepositoryPort,
    private val primaryProcessor: PaymentProcessorPort,
    private val fallbackProcessor: PaymentProcessorPort
) : ProcessPaymentPort, GetPaymentSummaryPort {

    override suspend fun processPayment(correlationId: UUID, amount: BigDecimal) {
        val now = Instant.now()
        try {
            primaryProcessor.processPayment(correlationId, amount)
            paymentRepository.save(Payment(correlationId, amount, "default", now))
        } catch (e: Exception) {
            try {
                fallbackProcessor.processPayment(correlationId, amount)
                paymentRepository.save(Payment(correlationId, amount, "fallback", now))
            } catch (fallbackException: Exception) {
                // Não registra pagamento se ambos falharem
            }
        }
    }

    override suspend fun getPaymentSummary(from: String?, to: String?): PaymentSummary {
        return paymentRepository.getSummary(from, to)
    }
}

