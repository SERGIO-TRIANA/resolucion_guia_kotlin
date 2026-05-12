package com.example.misfinanzas

// Importamos Serializable para poder pasar transacciones entre pantallas
import android.annotation.SuppressLint
import java.io.Serializable

// data class genera automáticamente toString(), equals(), hashCode() y copy()
// Implementa Serializable para poder enviarla con Intent.putExtra()
data class Transaccion(
    val id: Int,                    // identificador único
    val monto: Double,              // positivo = ingreso, negativo = gasto
    val descripcion: String,        // nombre descriptivo
    val categoria: Categoria,       // categoría del enum
    val fecha: Long = System.currentTimeMillis()  // timestamp de creación
) : Serializable {

    // Retorna true si el monto es positivo (ingreso)
    fun esIngreso(): Boolean = monto > 0

    // Retorna el monto formateado como moneda colombiana
    @SuppressLint("DefaultLocale")
    fun montoFormateado(): String {
        val signo = if (esIngreso()) "+" else ""
        return "$signo$ ${String.format("%,.0f", monto)}"
    }

    // Constantes y funciones de fábrica
    companion object {
        // Genera datos de ejemplo para desarrollo y pruebas
        fun datosDePrueba(): List<Transaccion> {
            return listOf(
                Transaccion(1, 2500000.0, "Salario mensual", Categoria.SALARIO),
                Transaccion(2, -150000.0, "Almuerzo restaurante", Categoria.COMIDA),
                Transaccion(3, -80000.0, "Uber al trabajo", Categoria.TRANSPORTE),
                Transaccion(4, -200000.0, "Recibo de luz", Categoria.SERVICIOS),
                Transaccion(5, 500000.0, "Proyecto web", Categoria.FREELANCE),
                Transaccion(6, -50000.0, "Netflix", Categoria.ENTRETENIMIENTO),
                Transaccion(7, -35000.0, "Medicinas", Categoria.SALUD),
                Transaccion(8, -120000.0, "Mercado", Categoria.COMIDA)
            )
        }
    }
}