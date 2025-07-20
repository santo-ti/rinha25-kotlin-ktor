package com.santo.rinha.infra.repositories

import com.santo.rinha.domain.Payment
import com.santo.rinha.domain.PaymentStatus
import com.santo.rinha.domain.PaymentSummary
import com.santo.rinha.domain.ports.PaymentRepositoryPort
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.sum
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

object PaymentsTable : UUIDTable("payments") {
    val transactionId = varchar("transaction_id", 36).uniqueIndex()
    val value = long("value")
    val description = varchar("description", 255)
    val status = varchar("status", 20)
    val fee = long("fee")
    val processedAt = long("processed_at")
}

class ExposedPaymentRepository : PaymentRepositoryPort {

    init {
        transaction {
            SchemaUtils.create(PaymentsTable)
        }
    }

    override suspend fun save(payment: Payment): Payment = newSuspendedTransaction {
        PaymentsTable.insert {
            it[transactionId] = payment.transactionId
            it[value] = payment.value
            it[description] = payment.description
            it[status] = payment.status.name
            it[fee] = payment.fee
            it[processedAt] = payment.processedAt
        }
        payment
    }

    override suspend fun batchSave(payments: List<Payment>): List<Payment> = newSuspendedTransaction {
        PaymentsTable.batchInsert(payments) { payment ->
            this[PaymentsTable.transactionId] = payment.transactionId
            this[PaymentsTable.value] = payment.value
            this[PaymentsTable.description] = payment.description
            this[PaymentsTable.status] = payment.status.name
            this[PaymentsTable.fee] = payment.fee
            this[PaymentsTable.processedAt] = payment.processedAt
        }
        payments
    }

    override suspend fun getSummary(): PaymentSummary = newSuspendedTransaction {
        val processedCount = PaymentsTable.select { PaymentsTable.status eq PaymentStatus.PROCESSED.name }.count()
        val failedCount = PaymentsTable.select { PaymentsTable.status eq PaymentStatus.FAILED.name }.count()

        val totalFee = PaymentsTable.slice(PaymentsTable.fee.sum())
            .select { PaymentsTable.status eq PaymentStatus.PROCESSED.name }
            .singleOrNull()?.get(PaymentsTable.fee.sum()) ?: 0L

        val totalValue = PaymentsTable.slice(PaymentsTable.value.sum())
            .select { PaymentsTable.status eq PaymentStatus.PROCESSED.name }
            .singleOrNull()?.get(PaymentsTable.value.sum()) ?: 0L

        PaymentSummary(
            totalProcessed = processedCount,
            totalFailed = failedCount,
            totalFee = totalFee,
            totalValue = totalValue
        )
    }
}

