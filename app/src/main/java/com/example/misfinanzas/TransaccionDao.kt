// TransaccionDao.kt
// DAO (Data Access Object) que define las operaciones sobre la tabla transacciones
package com.example.misfinanzas

// Importaciones de Room
import androidx.room.Dao      // marca la interfaz como un DAO
import androidx.room.Insert   // anotación para insertar registros
import androidx.room.Update   // anotación para actualizar registros
import androidx.room.Delete   // anotación para eliminar registros
import androidx.room.Query    // anotación para consultas SQL personalizadas
// LiveData permite que la UI se actualice automáticamente cuando los datos cambian
import androidx.lifecycle.LiveData

// @Dao marca esta interfaz como un Data Access Object
// Room genera la implementación automáticamente en tiempo de compilación
@Dao
interface TransaccionDao {

    // @Query ejecuta una consulta SQL personalizada
    // Retorna LiveData: la UI se actualiza automáticamente cuando la tabla cambia
    // Las funciones que retornan LiveData NO necesitan ser "suspend"
    // porque Room las ejecuta en segundo plano automáticamente
    @Query("SELECT * FROM transacciones ORDER BY fecha DESC")
    fun obtenerTodas(): LiveData<List<Transaccion>>

    // Consulta que filtra solo ingresos (monto > 0)
    @Query("SELECT * FROM transacciones WHERE monto > 0 ORDER BY fecha DESC")
    fun obtenerIngresos(): LiveData<List<Transaccion>>

    // Consulta que filtra solo gastos (monto < 0)
    @Query("SELECT * FROM transacciones WHERE monto < 0 ORDER BY fecha DESC")
    fun obtenerGastos(): LiveData<List<Transaccion>>

    // Consulta que suma todos los montos (balance total)
    // COALESCE retorna 0.0 si no hay registros (evita null)
    @Query("SELECT COALESCE(SUM(monto), 0.0) FROM transacciones")
    fun obtenerBalance(): LiveData<Double>

    // Consulta que suma solo los ingresos
    @Query("SELECT COALESCE(SUM(monto), 0.0) FROM transacciones WHERE monto > 0")
    fun obtenerTotalIngresos(): LiveData<Double>

    // Consulta que suma solo los gastos (valor absoluto)
    @Query("SELECT COALESCE(SUM(ABS(monto)), 0.0) FROM transacciones WHERE monto < 0")
    fun obtenerTotalGastos(): LiveData<Double>

    // Cuenta el número total de transacciones
    @Query("SELECT COUNT(*) FROM transacciones")
    fun obtenerCantidad(): LiveData<Int>

    // @Insert inserta un registro en la tabla
    // "suspend" indica que esta función se ejecuta en una coroutine (segundo plano)
    // Las operaciones de escritura DEBEN ser suspend para no bloquear el hilo principal
    @Insert
    suspend fun insertar(transaccion: Transaccion)

    // Inserta múltiples registros de una vez
    @Insert
    suspend fun insertarTodas(transacciones: List<Transaccion>)

    // @Update actualiza un registro existente (busca por primary key)
    @Update
    suspend fun actualizar(transaccion: Transaccion)

    // @Delete elimina un registro (busca por primary key)
    @Delete
    suspend fun eliminar(transaccion: Transaccion)

    // Elimina todos los registros de la tabla
    @Query("DELETE FROM transacciones")
    suspend fun eliminarTodas()
}