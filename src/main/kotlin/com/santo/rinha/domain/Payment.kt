package com.santo.rinha.domain

import kotlinx.serialization.Serializable
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

@Serializable
data class Payment(
    val correlationId: UUID,
    val amount: BigDecimal,
    val processor: String, // 'default' ou 'fallback'
    val processedAt: Instant
)