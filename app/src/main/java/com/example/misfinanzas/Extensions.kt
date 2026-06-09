// Extensions.kt
// Funciones de extensión reutilizables en toda la app
package com.example.misfinanzas

// Activity es la clase base de todas las pantallas
import android.app.Activity
// Toast muestra mensajes breves
import android.widget.Toast
// View es la clase base de todos los elementos de UI
import android.view.View

// Extension function para Activity: muestra un Toast de forma simple
// En lugar de escribir Toast.makeText(this, "mensaje", Toast.LENGTH_SHORT).show()
// se puede escribir: mostrarToast("mensaje")
fun Activity.mostrarToast(mensaje: String) {
    Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
}

// Extension function para View: muestra la vista
// En lugar de: binding.tvError.visibility = View.VISIBLE
// se puede escribir: binding.tvError.mostrar()
fun View.mostrar() {
    visibility = View.VISIBLE
}

// Extension function para View: oculta la vista (sin ocupar espacio)
fun View.ocultar() {
    visibility = View.GONE
}

// Extension function para View: oculta la vista (ocupando espacio)
fun View.invisible() {
    visibility = View.INVISIBLE
}

// Extension function para Double: formatea como moneda colombiana
// En lugar de: "$ ${String.format("%,.0f", monto)}"
// se puede escribir: monto.formatearCOP()
fun Double.formatearCOP(): String {
    return "$ ${String.format("%,.0f", this)}"
}

// Extension function para Double: formatea con signo
fun Double.formatearConSigno(): String {
    val signo = if (this >= 0) "+" else ""
    return "$signo$ ${String.format("%,.0f", this)}"
}
