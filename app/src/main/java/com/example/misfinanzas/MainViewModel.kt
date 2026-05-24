// MainViewModel.kt
// ViewModel que usa Room para persistir las transacciones
package com.example.misfinanzas

// Application se necesita para obtener el contexto de la app
import android.app.Application
// AndroidViewModel recibe Application como parámetro (a diferencia de ViewModel)
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
// viewModelScope proporciona un scope de coroutines ligado al ciclo de vida del ViewModel
import androidx.lifecycle.viewModelScope
// launch inicia una coroutine
import kotlinx.coroutines.launch

// AndroidViewModel en lugar de ViewModel porque necesitamos el contexto
// para acceder a la base de datos
// NUNCA guardar una referencia a una Activity en el ViewModel (causa memory leaks)
// Application es seguro porque vive tanto como la app
class MainViewModel(application: Application) : AndroidViewModel(application) {

    // Obtener el DAO de la base de datos
    // El DAO es nuestra puerta de entrada a la base de datos
    private val dao = AppDatabase.obtenerInstancia(application).transaccionDao()

    // LiveData que viene directamente de Room
    // Room actualiza estos LiveData automáticamente cuando la tabla cambia
    // No necesitamos MutableLiveData porque Room se encarga de todo
    val transacciones: LiveData<List<Transaccion>> = dao.obtenerTodas()
    val balance: LiveData<Double> = dao.obtenerBalance()
    val ingresos: LiveData<Double> = dao.obtenerTotalIngresos()
    val gastos: LiveData<Double> = dao.obtenerTotalGastos()
    val cantidad: LiveData<Int> = dao.obtenerCantidad()

    // init se ejecuta al crear el ViewModel
    init {
        // No cargamos datos aquí porque LiveData de Room necesita un observer activo
        // para tener un .value. La carga de datos de prueba se hace desde la Activity
        // cuando observa que la lista está vacía (ver MainActivity.kt)
    }

    // Función pública para insertar datos de prueba
    // Se llama desde la Activity cuando detecta que la lista está vacía
    fun insertarDatosDePrueba() {
        viewModelScope.launch {
            // insertarTodas es una función suspend del DAO
            // Se ejecuta en segundo plano gracias a la coroutine
            dao.insertarTodas(Transaccion.datosDePrueba())
        }
    }

    // Agrega una nueva transacción a la base de datos
    fun agregarTransaccion(transaccion: Transaccion) {
        // launch inicia una coroutine para ejecutar la operación en segundo plano
        viewModelScope.launch {
            // insertar es suspend: no bloquea el hilo principal
            dao.insertar(transaccion)
            // No necesitamos actualizar LiveData manualmente
            // Room notifica automáticamente a todos los observadores
        }
    }

    // Elimina una transacción de la base de datos
    fun eliminarTransaccion(transaccion: Transaccion) {
        viewModelScope.launch {
            dao.eliminar(transaccion)
        }
    }

    // Elimina todas las transacciones
    fun eliminarTodas() {
        viewModelScope.launch {
            dao.eliminarTodas()
        }
    }
}