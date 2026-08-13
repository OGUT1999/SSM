package com.example.engine

data class RiskScenarioResult(
    val scenarioName: String,
    val priceChangePercent: Double,
    val yieldChangePercent: Double,
    val costChangePercent: Double,
    val simulatedPricePerUnit: Double,
    val simulatedYieldVolume: Double,
    val simulatedTotalCost: Double,
    val simulatedRevenue: Double,
    val simulatedProfitLoss: Double,
    val simulatedRoiPercent: Double,
    val statusText: String, // "UNTUNG SANGAT BAIK", "AMBIL UNTUNG TIPIS", "IMPAS / BEP", "POTENSI RUGI"
    val riskAdvice: String
)

object RiskSimulationEngine {

    fun runRiskSimulation(
        baseCostBreakdown: CostBreakdown,
        basePricePerUnit: Double
    ): List<RiskScenarioResult> {
        val baseYield = baseCostBreakdown.totalProductionVolume.coerceAtLeast(1.0)
        val baseCost = baseCostBreakdown.totalProductionCost.coerceAtLeast(100.0)
        val unit = baseCostBreakdown.unit

        val scenarios = listOf(
            Triple("Kondisi Normal (Baseline)", 0.0, 0.0),
            Triple("Harga Pasar Turun -10%", -0.10, 0.0),
            Triple("Harga Pasar Turun -20%", -0.20, 0.0),
            Triple("Harga Pasar Turun -30% (Ekstrem)", -0.30, 0.0),
            Triple("Panen / Hasil Turun -10% (Hama Biasa)", 0.0, -0.10),
            Triple("Panen / Hasil Turun -30% (Cuaca Buruk)", 0.0, -0.30),
            Triple("Panen / Hasil Turun -50% (Gagal Sebagian)", 0.0, -0.50),
            Triple("Biaya Operasional / Pakan Naik +20%", 0.0, 0.0) // Handled with costChange
        )

        return scenarios.mapIndexed { idx, (title, pricePct, yieldPct) ->
            val costPct = if (idx == 7) 0.20 else 0.0

            val simPrice = (basePricePerUnit * (1.0 + pricePct)).coerceAtLeast(1.0)
            val simYield = (baseYield * (1.0 + yieldPct)).coerceAtLeast(0.1)
            val simCost = (baseCost * (1.0 + costPct)).coerceAtLeast(100.0)

            val simRev = simPrice * simYield
            val simProfit = simRev - simCost
            val simRoi = if (simCost > 0) (simProfit / simCost) * 100.0 else 0.0

            val status = when {
                simProfit >= (simCost * 0.25) -> "UNTUNG SANGAT BAIK 🟢"
                simProfit > 0 -> "UNTUNG TIPIS 🟡"
                simProfit == 0.0 -> "IMPAS / BEP ⚪"
                else -> "POTENSI RUGI 🔴"
            }

            val advice = when {
                simProfit < 0 -> "Perlu efisiensi biaya sebesar Rp${(-simProfit).toInt()} atau naikkan harga jual dengan pengolahan produk."
                simProfit < (simCost * 0.10) -> "Margin sangat tipis. Pastikan memperhitungkan biaya penyusutan dan simpanan risiko."
                else -> "Proyeksi keuangan aman. Lanjutkan pemantauan rutin cuaca dan pasar."
            }

            RiskScenarioResult(
                scenarioName = title,
                priceChangePercent = pricePct * 100,
                yieldChangePercent = yieldPct * 100,
                costChangePercent = costPct * 100,
                simulatedPricePerUnit = simPrice,
                simulatedYieldVolume = simYield,
                simulatedTotalCost = simCost,
                simulatedRevenue = simRev,
                simulatedProfitLoss = simProfit,
                simulatedRoiPercent = simRoi,
                statusText = status,
                riskAdvice = advice
            )
        }
    }
}
