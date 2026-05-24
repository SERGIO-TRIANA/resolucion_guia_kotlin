// AgregarActivity.kt
// Pantalla para agregar una nueva transacción (actualizada para Room)
package com.example.misfinanzas

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.misfinanzas.databinding.ActivityAgregarBinding

class AgregarActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAgregarBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAgregarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configurarSpinnerCategorias()
        configurarBotones()
    }

    // Llena el Spinner con las categorías del enum
    private fun configurarSpinnerCategorias() {
        val etiquetas = Categoria.values().map { "${it.emoji} ${it.etiqueta}" }
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            etiquetas
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spCategoria.adapter = adapter
    }

    // Configura las acciones de los botones
    private fun configurarBotones() {
        binding.btnGuardar.setOnClickListener {
            val transaccion = crearTransaccion()
            if (transaccion != null) {
                devolverResultado(transaccion)
            }
        }

        binding.btnCancelar.setOnClickListener {
            finish()
        }
    }

    // Valida los campos y crea una Transaccion
    private fun crearTransaccion(): Transaccion? {
        val descripcion = binding.etDescripcion.text.toString().trim()
        val montoTexto = binding.etMonto.text.toString().trim()

        // Validar descripción
        if (descripcion.isEmpty()) {
            binding.tilDescripcion.error = "La descripción es obligatoria"
            return null
        } else {
            binding.tilDescripcion.error = null
        }

        // Validar monto
        if (montoTexto.isEmpty()) {
            binding.tilMonto.error = "El monto es obligatorio"
            return null
        }

        val montoNumero = montoTexto.toDoubleOrNull()
        if (montoNumero == null || montoNumero <= 0) {
            binding.tilMonto.error = "Ingresa un monto válido mayor a 0"
            return null
        } else {
            binding.tilMonto.error = null
        }

        // Determinar signo según tipo
        val esGasto = binding.rbGasto.isChecked
        val montoFinal = if (esGasto) -montoNumero else montoNumero

        // Obtener la categoría seleccionada
        val categoriaIndex = binding.spCategoria.selectedItemPosition
        val categoria = Categoria.values()[categoriaIndex]

        // Crear la transacción con categoriaNombre (String) para Room
        // id = 0 para que Room lo genere automáticamente (autoGenerate)
        return Transaccion(
            id = 0,
            monto = montoFinal,
            descripcion = descripcion,
            categoriaNombre = categoria.name  // .name convierte el enum a String
        )
    }

    // Devuelve la transacción a la Activity anterior
    private fun devolverResultado(transaccion: Transaccion) {
        val resultadoIntent = Intent()
        resultadoIntent.putExtra("NUEVA_TRANSACCION", transaccion)
        setResult(RESULT_OK, resultadoIntent)
        finish()
    }
}