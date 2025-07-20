package com.santo.rinha.application.services

import com.santo.rinha.domain.Payment
import com.santo.rinha.domain.PaymentStatus
import com.santo.rinha.domain.PaymentSummary
import com.santo.rinha.domain.ports.GetPaymentSummaryPort
import com.santo.rinha.domain.ports.PaymentProcessorPort
import com.santo.rinha.domain.ports.PaymentRepositoryPort
import com.santo.rinha.domain.ports.ProcessPaymentPort
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class PaymentService(
    private val paymentRepository: PaymentRepositoryPort,
    private val primaryProcessor: PaymentProcessorPort,
    private val fallbackProcessor: PaymentProcessorPort
) : ProcessPaymentPort, GetPaymentSummaryPort {

    override suspend fun processPayment(value: Long, description: String): Payment {
        return try {
            // Try primary processor first
            val payment = primaryProcessor.processPayment(value, description)
            paymentRepository.save(payment)
        } catch (e: Exception) {
            // Fallback to secondary processor
            try {
                val payment = fallbackProcessor.processPayment(value, description)
                paymentRepository.save(payment)
            } catch (fallbackException: Exception) {
                // Both failed, create failed payment record
                val failedPayment = Payment(
                    value = value,
                    description = description,
                    status = PaymentStatus.FAILED,
                    fee = 0L
                )
                paymentRepository.save(failedPayment)
            }
        }
    }

    override suspend fun getPaymentSummary(): PaymentSummary {
        return paymentRepository.getSummary()
    }

    // Batch processing for high throughput scenarios
    suspend fun processBatchPayments(payments: List<Pair<Long, String>>): List<Payment> = coroutineScope {
        val processedPayments = payments.map { (value, description) ->
            async { processPayment(value, description) }
        }.map { it.await() }

        // Batch save for better performance
        paymentRepository.batchSave(processedPayments)
    }
}

