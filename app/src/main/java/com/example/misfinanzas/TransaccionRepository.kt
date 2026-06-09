// TransaccionRepository.kt
// Repository que abstrae el acceso a datos
// El ViewModel usa el Repository sin saber si los datos vienen de Room o de la API
package com.example.misfinanzas

import androidx.lifecycle.LiveData
import com.example.misfinanzas.api.RetrofitClient
import com.example.misfinanzas.api.TasaCambioResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// El Repository recibe el DAO como dependencia (inyección de dependencias manual)
// Así es fácil de testear: en tests se puede pasar un DAO falso
class TransaccionRepository(private val dao: TransaccionDao) {

    // ===== DATOS LOCALES (Room) =====

    // Exponer los LiveData del DAO directamente
    // El Repository no agrega lógica aquí, solo delega
    // En una app más compleja, podría combinar datos de varias fuentes
    val todasLasTransacciones: LiveData<List<Transaccion>> = dao.obtenerTodas()
    val balance: LiveData<Double> = dao.obtenerBalance()
    val totalIngresos: LiveData<Double> = dao.obtenerTotalIngresos()
    val totalGastos: LiveData<Double> = dao.obtenerTotalGastos()
    val cantidad: LiveData<Int> = dao.obtenerCantidad()

    // Insertar una transacción
    // suspend porque es una operación de escritura que debe ir en segundo plano
    suspend fun insertar(transaccion: Transaccion) {
        dao.insertar(transaccion)
    }

    // Insertar múltiples transacciones
    suspend fun insertarTodas(transacciones: List<Transaccion>) {
        dao.insertarTodas(transacciones)
    }

    // Eliminar una transacción
    suspend fun eliminar(transaccion: Transaccion) {
        dao.eliminar(transaccion)
    }

    // Eliminar todas las transacciones
    suspend fun eliminarTodas() {
        dao.eliminarTodas()
    }

    // ===== DATOS REMOTOS (API) =====

    // Consultar tasas de cambio desde la API
    suspend fun obtenerTasasCambio(monedaBase: String = "USD"): TasaCambioResponse =
        withContext(Dispatchers.IO) {
            RetrofitClient.apiService.obtenerTasas(monedaBase)
        }
}
