// AgregarActivity.kt
// Formulario con validación mejorada y limpieza de errores en tiempo real
package com.example.misfinanzas

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.widget.ArrayAdapter
import android.widget.Toast
// doAfterTextChanged simplifica el TextWatcher
import androidx.core.widget.doAfterTextChanged
import com.example.misfinanzas.databinding.ActivityAgregarBinding

class AgregarActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAgregarBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAgregarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configurarSpinnerCategorias()
        configurarValidacionEnTiempoReal()
        configurarBotones()
    }

    // Llena el Spinner con las categorías
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

    // Configura la limpieza de errores mientras el usuario escribe
    private fun configurarValidacionEnTiempoReal() {
        // Cuando el usuario escribe en descripción, limpiar el error
        // doAfterTextChanged es más simple que implementar TextWatcher completo
        binding.etDescripcion.doAfterTextChanged { texto ->
            // Si el texto no está vacío, limpiar el error
            if (!texto.isNullOrBlank()) {
                binding.tilDescripcion.error = null
            }
        }

        // Cuando el usuario escribe en monto, limpiar el error
        binding.etMonto.doAfterTextChanged { texto ->
            if (!texto.isNullOrBlank()) {
                binding.tilMonto.error = null
            }
        }
    }

    // Configura las acciones de los botones
    private fun configurarBotones() {
        binding.btnGuardar.setOnClickListener {
            // Validar todos los campos
            if (validarFormulario()) {
                // Si todo es válido, crear la transacción y devolverla
                val transaccion = crearTransaccion()
                devolverResultado(transaccion)
            }
        }

        binding.btnCancelar.setOnClickListener {
            finish()
        }
    }

    // Valida todos los campos del formulario
    // Retorna true si todo es válido, false si hay errores
    private fun validarFormulario(): Boolean {
        // Variable que rastrea si todo es válido
        var esValido = true

        // Validar descripción
        val descripcion = binding.etDescripcion.text.toString().trim()
        if (descripcion.isEmpty()) {
            binding.tilDescripcion.error = "La descripción es obligatoria"
            esValido = false
        } else if (descripcion.length < 3) {
            binding.tilDescripcion.error = "Mínimo 3 caracteres"
            esValido = false
        } else {
            binding.tilDescripcion.error = null
        }

        // Validar monto
        val montoTexto = binding.etMonto.text.toString().trim()
        val montoNumero = montoTexto.toDoubleOrNull()
        if (montoTexto.isEmpty()) {
            binding.tilMonto.error = "El monto es obligatorio"
            esValido = false
        } else if (montoNumero == null) {
            binding.tilMonto.error = "Ingresa un número válido"
            esValido = false
        } else if (montoNumero <= 0) {
            binding.tilMonto.error = "El monto debe ser mayor a 0"
            esValido = false
        } else if (montoNumero > 999999999) {
            binding.tilMonto.error = "El monto es demasiado grande"
            esValido = false
        } else {
            binding.tilMonto.error = null
        }

        // No validamos esValido = false en cada caso porque queremos
        // mostrar TODOS los errores a la vez, no solo el primero
        return esValido
    }

    // Crea la transacción con los datos del formulario
    // Se llama solo después de que validarFormulario() retorna true
    private fun crearTransaccion(): Transaccion {
        val descripcion = binding.etDescripcion.text.toString().trim()
        val montoNumero = binding.etMonto.text.toString().trim().toDouble()
        val esGasto = binding.rbGasto.isChecked
        val montoFinal = if (esGasto) -montoNumero else montoNumero
        val categoriaIndex = binding.spCategoria.selectedItemPosition
        val categoria = Categoria.values()[categoriaIndex]

        return Transaccion(
            id = 0,
            monto = montoFinal,
            descripcion = descripcion,
            categoriaNombre = categoria.name
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