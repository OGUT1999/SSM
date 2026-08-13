package com.example.engine

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class DigitalReceipt(
    val receiptNo: String,
    val businessName: String,
    val counterpartyName: String,
    val itemName: String,
    val quantity: Double,
    val unit: String,
    val pricePerUnit: Double,
    val subtotal: Double,
    val shippingCost: Double,
    val totalAmount: Double,
    val dateText: String,
    val paymentStatus: String,
    val antiFraudNotice: String
)

object TransactionReceiptEngine {

    fun generateReceipt(
        businessName: String,
        counterpartyName: String,
        itemName: String,
        quantity: Double,
        unit: String,
        pricePerUnit: Double,
        shippingCost: Double = 0.0,
        paymentStatus: String = "LUNAS / SELESAI"
    ): DigitalReceipt {
        val dateStr = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale("id", "ID")).format(Date())
        val randomSuffix = (1000..9999).random()
        val receiptNo = "SSM-NOTA-${System.currentTimeMillis().toString().takeLast(6)}-$randomSuffix"

        val sub = quantity * pricePerUnit
        val total = sub + shippingCost

        return DigitalReceipt(
            receiptNo = receiptNo,
            businessName = businessName,
            counterpartyName = counterpartyName,
            itemName = itemName,
            quantity = quantity,
            unit = unit,
            pricePerUnit = pricePerUnit,
            subtotal = sub,
            shippingCost = shippingCost,
            totalAmount = total,
            dateText = dateStr,
            paymentStatus = paymentStatus,
            antiFraudNotice = "PENTING: Nota digital ini dibuat otomatis oleh SSM. Pastikan selalu memverifikasi uang benar-benar masuk ke rekening/kas sebelum menyerahkan barang fisik!"
        )
    }
}
