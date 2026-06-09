// TasaCambioResponse.kt
// Data class que representa la respuesta JSON de la API de tasas de cambio
package com.example.misfinanzas.api

// Gson convierte automáticamente los campos del JSON a propiedades de la clase
// Los nombres de las propiedades deben coincidir con las claves del JSON
// Si no coinciden, se usa @SerializedName para mapearlos
data class TasaCambioResponse(
    val base: String,                    // moneda base (ej: "USD")
    val rates: Map<String, Double>       // mapa de moneda -> tasa (ej: "COP" -> 4150.50)
    // Map<String, Double> porque "rates" es un objeto JSON con claves String y valores numéricos
)
