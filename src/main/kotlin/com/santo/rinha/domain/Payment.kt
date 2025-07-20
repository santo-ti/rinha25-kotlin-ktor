package com.santo.rinha.domain

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Payment(
    val transactionId: String = UUID.randomUUID().toString(),
    val value: Long,
    val description: String,
    val status: PaymentStatus,
    val fee: Long = 0L,
    val processedAt: Long = System.currentTimeMillis()
)

@Serializable
enum class PaymentStatus {
    PROCESSED,
    FAILED
}

@Serializable
data class PaymentSummary(
    val totalProcessed: Long,
    val totalFailed: Long,
    val totalFee: Long,
    val totalValue: Long
)