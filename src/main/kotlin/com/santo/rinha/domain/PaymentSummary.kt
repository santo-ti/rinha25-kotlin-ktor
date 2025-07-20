package com.santo.rinha.domain

import kotlinx.serialization.Serializable
import java.math.BigDecimal

@Serializable
data class PaymentSummary(
    val default: ProcessorSummary,
    val fallback: ProcessorSummary
)

@Serializable
data class ProcessorSummary(
    val totalRequests: Long,
    val totalAmount: BigDecimal
) 