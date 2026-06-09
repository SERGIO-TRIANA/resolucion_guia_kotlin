// ResultadoApi.kt
// Sealed class que representa los posibles estados de una llamada a la API
package com.example.misfinanzas.api

// sealed class es como un enum pero donde cada opción puede tener datos diferentes
// El compilador verifica que se manejen todos los casos en un "when"
sealed class ResultadoApi<out T> {
    // T es un tipo genérico: puede ser cualquier tipo de dato

    // Estado de carga: la petición está en progreso
    // "object" porque no necesita datos adicionales
    object Cargando : ResultadoApi<Nothing>()

    // Estado de éxito: la petición terminó bien y tenemos datos
    // "data class" porque necesita guardar los datos recibidos
    data class Exito<T>(val datos: T) : ResultadoApi<T>()

    // Estado de error: la petición falló
    // Guarda el mensaje de error para mostrarlo al usuario
    data class Error(val mensaje: String) : ResultadoApi<Nothing>()
}
