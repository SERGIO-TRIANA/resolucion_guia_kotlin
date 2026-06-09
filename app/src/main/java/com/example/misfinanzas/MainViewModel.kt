// MainViewModel.kt
// ViewModel refactorizado usando el patrón Repository
package com.example.misfinanzas

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.misfinanzas.api.ResultadoApi
import com.example.misfinanzas.api.TasaCambioResponse
import java.net.UnknownHostException
import java.net.SocketTimeoutException

class MainViewModel(application: Application) : AndroidViewModel(application) {

    // Crear el Repository con el DAO de la base de datos
    // El ViewModel solo conoce al Repository, no al DAO directamente
    private val repository: TransaccionRepository

    // LiveData que vienen del Repository (y por debajo, de Room)
    val transacciones: LiveData<List<Transaccion>>
    val balance: LiveData<Double>
    val ingresos: LiveData<Double>
    val gastos: LiveData<Double>
    val cantidad: LiveData<Int>

    // LiveData para el resultado de la API
    private val _tasasCambio = MutableLiveData<ResultadoApi<TasaCambioResponse>>()
    val tasasCambio: LiveData<ResultadoApi<TasaCambioResponse>> = _tasasCambio

    // init se ejecuta al crear el ViewModel
    init {
        // Obtener el DAO y crear el Repository
        val dao = AppDatabase.obtenerInstancia(application).transaccionDao()
        repository = TransaccionRepository(dao)

        // Asignar los LiveData del Repository
        transacciones = repository.todasLasTransacciones
        balance = repository.balance
        ingresos = repository.totalIngresos
        gastos = repository.totalGastos
        cantidad = repository.cantidad
    }

    // ===== OPERACIONES LOCALES =====

    // Agregar una transacción
    fun agregarTransaccion(transaccion: Transaccion) {
        viewModelScope.launch {
            repository.insertar(transaccion)
        }
    }

    // Eliminar una transacción
    fun eliminarTransaccion(transaccion: Transaccion) {
        viewModelScope.launch {
            repository.eliminar(transaccion)
        }
    }

    // Insertar datos de prueba
    fun insertarDatosDePrueba() {
        viewModelScope.launch {
            repository.insertarTodas(Transaccion.datosDePrueba())
        }
    }

    // Eliminar todas las transacciones
    fun eliminarTodas() {
        viewModelScope.launch {
            repository.eliminarTodas()
        }
    }

    // ===== OPERACIONES REMOTAS =====

    // Consultar tasas de cambio
    fun consultarTasas(monedaBase: String = "USD") {
        _tasasCambio.value = ResultadoApi.Cargando

        viewModelScope.launch {
            try {
                val respuesta = withContext(Dispatchers.IO) {
                    repository.obtenerTasasCambio(monedaBase)
                }
                _tasasCambio.value = ResultadoApi.Exito(respuesta)
            } catch (e: UnknownHostException) {
                _tasasCambio.value = ResultadoApi.Error("Sin conexión a internet")
            } catch (e: SocketTimeoutException) {
                _tasasCambio.value = ResultadoApi.Error("Tiempo de espera agotado")
            } catch (e: Exception) {
                _tasasCambio.value = ResultadoApi.Error("Error: ${e.message}")
            }
        }
    }
}
