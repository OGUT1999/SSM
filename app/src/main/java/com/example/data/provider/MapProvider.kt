package com.example.data.provider

interface MapProvider {
    val providerName: String
    suspend fun getRegionalLocationName(latitude: Double, longitude: Double): String
}

class DeviceGpsMapProvider(
    private val costControlEngine: CostControlEngine
) : MapProvider {

    override val providerName: String = "Device GPS + Regional Cache ($0)"

    override suspend fun getRegionalLocationName(latitude: Double, longitude: Double): String {
        costControlEngine.updateMapProvider(providerName)
        return "Kecamatan Natar, Kabupaten Lampung Selatan"
    }
}
