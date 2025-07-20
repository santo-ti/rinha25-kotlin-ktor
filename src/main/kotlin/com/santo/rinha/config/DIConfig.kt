package com.santo.rinha.config

import com.santo.rinha.application.services.PaymentService
import com.santo.rinha.infra.repositories.ExposedPaymentRepository
import com.santo.rinha.infra.repositories.HttpPaymentProcessor
import io.ktor.server.application.Application

object DIConfig {
    fun Application.configureDependencyInjection() {
        val paymentRepository = ExposedPaymentRepository()

        val primaryProcessor = HttpPaymentProcessor(
            baseUrl = System.getenv("PRIMARY_PROCESSOR_URL") ?: "http://payment-processor-default:8080",
            timeoutMs = 3000L
        )

        val fallbackProcessor = HttpPaymentProcessor(
            baseUrl = System.getenv("FALLBACK_PROCESSOR_URL") ?: "http://payment-processor-fallback:8080",
            timeoutMs = 3000L
        )

        val paymentService = PaymentService(paymentRepository, primaryProcessor, fallbackProcessor)

        DependencyContainer.processPaymentPort = paymentService
        DependencyContainer.getPaymentSummaryPort = paymentService
    }
}