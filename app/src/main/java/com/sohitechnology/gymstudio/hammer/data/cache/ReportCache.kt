package com.sohitechnology.gymstudio.hammer.data.cache

import com.sohitechnology.gymstudio.hammer.data.model.ReportData
import com.sohitechnology.gymstudio.hammer.data.model.TransactionData

object ReportCache {
    var reports: List<ReportData>? = null
    var transactions: List<TransactionData>? = null
    var totalAmount: Int = 0
    var totalCount: Int = 0

    fun clear() {
        reports = null
        transactions = null
        totalAmount = 0
        totalCount = 0
    }
}
