// ApiService.kt
// Interfaz que define los endpoints de la API
package com.example.misfinanzas.api

import retrofit2.http.GET
import retrofit2.http.Path

// Cada función de esta interfaz es un endpoint de la API
interface ApiService {

    // La API de ExchangeRate-API v4 espera la moneda directamente en la ruta:
    // https://api.exchangerate-api.com/v4/latest/USD
    // Si usamos @Query, se generaría .../latest?base=USD, lo cual devuelve un error HTML (causando el fallo de JSON)
    @GET("latest/{moneda}")
    suspend fun obtenerTasas(
        // @Path reemplaza el marcador {moneda} en la URL
        @Path("moneda") monedaBase: String
    ): TasaCambioResponse
}
