// MainViewModel.kt
// ViewModel que maneja el estado de la pantalla principal
// Sobrevive a rotaciones de pantalla y cambios de configuración
package com.example.misfinanzas

// ViewModel es la clase base para ViewModels
import androidx.lifecycle.ViewModel
// LiveData es un contenedor de datos observable
// MutableLiveData permite modificar el valor, LiveData solo permite observar
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

// MainViewModel hereda de ViewModel
// No recibe contexto ni referencia a la Activity (eso causaría memory leaks)
class MainViewModel : ViewModel() {

    // _transacciones es MutableLiveData: se puede modificar desde dentro del ViewModel
    // Es privado para que la Activity no lo modifique directamente
    // El guion bajo "_" es una convención para indicar que es la versión mutable privada
    private val _transacciones = MutableLiveData<List<Transaccion>>()

    // transacciones es LiveData (solo lectura): la Activity puede observar pero no modificar
    // Esta es la versión pública que expone el ViewModel
    val transacciones: LiveData<List<Transaccion>> = _transacciones

    // LiveData para el balance calculado
    private val _balance = MutableLiveData<Double>()
    val balance: LiveData<Double> = _balance

    // LiveData para los ingresos totales
    private val _ingresos = MutableLiveData<Double>()
    val ingresos: LiveData<Double> = _ingresos

    // LiveData para los gastos totales
    private val _gastos = MutableLiveData<Double>()
    val gastos: LiveData<Double> = _gastos

    // init se ejecuta cuando se crea el ViewModel (una sola vez)
    // Cargamos los datos de prueba iniciales
    init {
        // Cargar datos de ejemplo
        _transacciones.value = Transaccion.datosDePrueba()
        // Recalcular los totales
        recalcularTotales()
    }

    // Agrega una nueva transacción al inicio de la lista
    fun agregarTransaccion(transaccion: Transaccion) {
        // Obtener la lista actual (o una lista vacía si es null)
        val listaActual = _transacciones.value?.toMutableList() ?: mutableListOf()
        // Agregar al inicio (posición 0)
        listaActual.add(0, transaccion)
        // Actualizar el LiveData con la nueva lista
        // Al cambiar .value, todos los observadores son notificados automáticamente
        _transacciones.value = listaActual
        // Recalcular totales
        recalcularTotales()
    }

    // Elimina una transacción de la lista por su posición
    fun eliminarTransaccion(posicion: Int) {
        val listaActual = _transacciones.value?.toMutableList() ?: return
        // Verificar que la posición sea válida
        if (posicion in listaActual.indices) {
            listaActual.removeAt(posicion)
            _transacciones.value = listaActual
            recalcularTotales()
        }
    }

    // Recalcula balance, ingresos y gastos a partir de la lista actual
    private fun recalcularTotales() {
        val lista = _transacciones.value ?: emptyList()
        // sumOf aplica una transformación a cada elemento y suma los resultados
        _ingresos.value = lista.filter { it.esIngreso() }.sumOf { it.monto }
        _gastos.value = lista.filter { !it.esIngreso() }.sumOf { Math.abs(it.monto) }
        _balance.value = lista.sumOf { it.monto }
    }
}