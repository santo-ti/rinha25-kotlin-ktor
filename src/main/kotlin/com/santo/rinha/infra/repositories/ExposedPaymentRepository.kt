package com.santo.rinha.infra.repositories

import com.santo.rinha.domain.Payment
import com.santo.rinha.domain.PaymentSummary
import com.santo.rinha.domain.ProcessorSummary
import com.santo.rinha.domain.ports.PaymentRepositoryPort
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.Query
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.javatime.timestamp
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant

object PaymentsTable : UUIDTable("payments") {
    val correlationId = uuid("correlation_id").uniqueIndex()
    val amount = decimal("amount", 20, 2)
    val processor = varchar("processor", 16)
    val processedAt = timestamp("processed_at")
}

class ExposedPaymentRepository : PaymentRepositoryPort {
    init {
        transaction {
            SchemaUtils.create(PaymentsTable)
        }
    }

    override suspend fun save(payment: Payment): Unit = newSuspendedTransaction {
        PaymentsTable.insert {
            it[correlationId] = payment.correlationId
            it[amount] = payment.amount
            it[processor] = payment.processor
            it[processedAt] = payment.processedAt
        }
    }

    override suspend fun getSummary(from: String?, to: String?): PaymentSummary = newSuspendedTransaction {
        val fromInstant = from?.let { Instant.parse(it) }
        val toInstant = to?.let { Instant.parse(it) }

        fun Query.filterByDate(): Query {
            var q = this
            if (fromInstant != null) q = q.andWhere { PaymentsTable.processedAt greaterEq fromInstant }
            if (toInstant != null) q = q.andWhere { PaymentsTable.processedAt lessEq toInstant }
            return q
        }

        val defaultQuery = PaymentsTable.selectAll().where { PaymentsTable.processor eq "default" }.filterByDate()
        val fallbackQuery = PaymentsTable.selectAll().where { PaymentsTable.processor eq "fallback" }.filterByDate()

        val defaultTotalRequests = defaultQuery.count()
        val defaultTotalAmount = defaultQuery.sumOf { it[PaymentsTable.amount] }
        val fallbackTotalRequests = fallbackQuery.count()
        val fallbackTotalAmount = fallbackQuery.sumOf { it[PaymentsTable.amount] }

        PaymentSummary(
            default = ProcessorSummary(defaultTotalRequests, defaultTotalAmount),
            fallback = ProcessorSummary(fallbackTotalRequests, fallbackTotalAmount)
        )
    }
}

