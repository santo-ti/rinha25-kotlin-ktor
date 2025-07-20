package com.santo.rinha.config

import com.santo.rinha.domain.ports.GetPaymentSummaryPort
import com.santo.rinha.domain.ports.ProcessPaymentPort

// Global dependency container (simple DI for performance)
object DependencyContainer {
    lateinit var processPaymentPort: ProcessPaymentPort
    lateinit var getPaymentSummaryPort: GetPaymentSummaryPort
}