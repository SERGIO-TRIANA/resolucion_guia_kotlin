// Transaccion.kt
// Entity de Room que representa la tabla "transacciones" en la base de datos
package com.example.misfinanzas

// Importaciones de Room
// @Entity marca esta clase como una tabla de la base de datos
import androidx.room.Entity
// @PrimaryKey marca el campo que es la clave primaria
import androidx.room.PrimaryKey
import java.io.Serializable

// @Entity indica que esta clase es una tabla en la base de datos
// tableName define el nombre de la tabla en SQLite
@Entity(tableName = "transacciones")
data class Transaccion(
    // @PrimaryKey marca este campo como la clave primaria
    // autoGenerate = true hace que Room genere el id automáticamente (autoincrement)
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,                    // 0 = Room asignará el id al insertar

    val monto: Double,                  // positivo = ingreso, negativo = gasto
    val descripcion: String,            // nombre descriptivo
    val categoriaNombre: String,        // nombre de la categoría como String
    // Room no puede guardar enums directamente, así que guardamos el nombre
    // y lo convertimos a enum cuando lo necesitemos
    val fecha: Long = System.currentTimeMillis()  // timestamp de creación
) : Serializable {

    // Propiedad calculada que convierte el String a enum Categoria
    // No se guarda en la base de datos porque es un getter sin backing field
    // Room solo persiste las propiedades del constructor primario
    val categoria: Categoria
        get() = try {
            // valueOf busca un valor del enum por su nombre
            Categoria.valueOf(categoriaNombre)
        } catch (e: Exception) {
            // Si el nombre no coincide con ningún valor, usar OTROS
            Categoria.OTROS
        }

    // Retorna true si el monto es positivo (ingreso)
    fun esIngreso(): Boolean = monto > 0

    // Retorna el monto formateado como moneda colombiana
    fun montoFormateado(): String {
        val signo = if (esIngreso()) "+" else ""
        return "$signo$ ${String.format("%,.0f", monto)}"
    }

    // Funciones de fábrica y datos de prueba
    companion object {
        // Genera datos de ejemplo para desarrollo y pruebas
        fun datosDePrueba(): List<Transaccion> {
            return listOf(
                Transaccion(1, 2500000.0, "Salario mensual", Categoria.SALARIO.name),
                Transaccion(2, -150000.0, "Almuerzo restaurante", Categoria.COMIDA.name),
                Transaccion(3, -80000.0, "Uber al trabajo", Categoria.TRANSPORTE.name),
                Transaccion(4, -200000.0, "Recibo de luz", Categoria.SERVICIOS.name),
                Transaccion(5, 500000.0, "Proyecto web", Categoria.FREELANCE.name),
                Transaccion(6, -50000.0, "Netflix", Categoria.ENTRETENIMIENTO.name),
                Transaccion(7, -35000.0, "Medicinas", Categoria.SALUD.name),
                Transaccion(8, -120000.0, "Mercado", Categoria.COMIDA.name)
            )
        }
    }
}