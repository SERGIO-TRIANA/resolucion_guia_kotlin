// RetrofitClient.kt
// Singleton que configura y proporciona la instancia de Retrofit
package com.example.misfinanzas.api

// Retrofit es el builder principal
import retrofit2.Retrofit
// GsonConverterFactory convierte JSON a objetos Kotlin
import retrofit2.converter.gson.GsonConverterFactory

// "object" en Kotlin crea un Singleton (una sola instancia en toda la app)
// No se necesita crear instancias con "new" ni manejar el patrón manualmente
object RetrofitClient {

    // URL base de la API (todas las rutas se concatenan a esta)
    // Debe terminar en "/" para que Retrofit concatene correctamente
    private const val BASE_URL = "https://api.exchangerate-api.com/v4/"

    // "by lazy" significa que solo se crea cuando se usa por primera vez
    // Después de crearse, se reutiliza la misma instancia
    val apiService: ApiService by lazy {
        Retrofit.Builder()
            // URL base de todas las peticiones
            .baseUrl(BASE_URL)
            // Agregar el converter de Gson para parsear JSON automáticamente
            .addConverterFactory(GsonConverterFactory.create())
            // Construir la instancia de Retrofit
            .build()
            // Crear la implementación de la interfaz ApiService
            .create(ApiService::class.java)
    }
}
