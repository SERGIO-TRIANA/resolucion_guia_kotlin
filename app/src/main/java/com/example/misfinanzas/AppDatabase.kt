// AppDatabase.kt
// Clase principal de la base de datos Room
package com.example.misfinanzas

// Context se necesita para crear la base de datos
import android.content.Context
// Importaciones de Room
import androidx.room.Database   // marca la clase como una base de datos Room
import androidx.room.Room       // builder para crear la instancia
import androidx.room.RoomDatabase  // clase base

// @Database define:
// - entities: las tablas de la base de datos (array de clases @Entity)
// - version: número de versión (incrementar cuando se cambia el esquema)
// - exportSchema: si se exporta el esquema a un archivo JSON (false para simplificar)
@Database(
    entities = [Transaccion::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    // Función abstracta que retorna el DAO
    // Room genera la implementación automáticamente
    abstract fun transaccionDao(): TransaccionDao

    // companion object implementa el patrón Singleton
    // Garantiza que solo exista UNA instancia de la base de datos en toda la app
    companion object {
        // @Volatile asegura que los cambios a INSTANCE sean visibles
        // inmediatamente en todos los hilos
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Función que retorna la instancia única de la base de datos
        // Si no existe, la crea. Si ya existe, la retorna.
        fun obtenerInstancia(context: Context): AppDatabase {
            // Si ya existe una instancia, retornarla directamente
            // ?: (Elvis operator) ejecuta el bloque de la derecha si INSTANCE es null
            return INSTANCE ?: synchronized(this) {
                // synchronized evita que dos hilos creen la instancia al mismo tiempo
                // (condición de carrera)

                // Crear la base de datos usando el builder de Room
                val instancia = Room.databaseBuilder(
                    context.applicationContext,  // contexto de la aplicación (no de la Activity)
                    AppDatabase::class.java,     // clase de la base de datos
                    "misfinanzas_database"       // nombre del archivo de la base de datos
                )
                    // fallbackToDestructiveMigration: si la versión cambia, borra y recrea
                    // En producción se usarían migraciones, pero para aprender es más simple así
                    .fallbackToDestructiveMigration()
                    .build()

                // Guardar la instancia para reutilizarla
                INSTANCE = instancia
                // Retornar la instancia creada
                instancia
            }
        }
    }
}