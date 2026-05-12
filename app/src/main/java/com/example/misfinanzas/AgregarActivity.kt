// AgregarActivity.kt
// Pantalla para agregar una nueva transacción
package com.example.misfinanzas

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
// Intent se usa para pasar datos entre Activities
import android.content.Intent
// ArrayAdapter conecta un array de datos con un Spinner (dropdown)
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.misfinanzas.databinding.ActivityAgregarBinding

class AgregarActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAgregarBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflar el layout de esta pantalla
        binding = ActivityAgregarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurar el Spinner (dropdown) con las categorías del enum
        configurarSpinnerCategorias()

        // Configurar los botones
        configurarBotones()
    }

    // Llena el Spinner con las categorías del enum Categoria
    private fun configurarSpinnerCategorias() {
        // Obtener las etiquetas de todas las categorías
        // .values() retorna todos los valores del enum
        // .map { } transforma cada valor en su etiqueta legible
        val etiquetas = Categoria.values().map { "${it.emoji} ${it.etiqueta}" }

        // ArrayAdapter conecta la lista de strings con el Spinner
        // android.R.layout.simple_spinner_item = layout predefinido para cada item
        val adapter = ArrayAdapter(
            this,                                    // contexto
            android.R.layout.simple_spinner_item,    // layout de cada item
            etiquetas                                // datos
        )
        // setDropDownViewResource define cómo se ve el menú desplegable
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Asignar el adapter al Spinner
        binding.spCategoria.adapter = adapter
    }

    // Configura las acciones de los botones
    private fun configurarBotones() {
        // Botón GUARDAR: valida los campos y devuelve la transacción
        binding.btnGuardar.setOnClickListener {
            // Intentar crear la transacción
            val transaccion = crearTransaccion()
            if (transaccion != null) {
                // Si la validación pasó, devolver el resultado a la pantalla anterior
                devolverResultado(transaccion)
            }
        }

        // Botón CANCELAR: cierra esta pantalla sin devolver nada
        binding.btnCancelar.setOnClickListener {
            // finish() cierra la Activity actual y regresa a la anterior
            finish()
        }
    }

    // Valida los campos y crea una Transaccion, o retorna null si hay errores
    private fun crearTransaccion(): Transaccion? {
        // Obtener los valores de los campos
        // .toString().trim() convierte a String y elimina espacios al inicio/final
        val descripcion = binding.etDescripcion.text.toString().trim()
        val montoTexto = binding.etMonto.text.toString().trim()

        // Validar descripción
        if (descripcion.isEmpty()) {
            // .error muestra un mensaje de error debajo del campo
            binding.tilDescripcion.error = "La descripción es obligatoria"
            return null  // retorna null para indicar que la validación falló
        } else {
            // Limpiar el error si el campo es válido
            binding.tilDescripcion.error = null
        }

        // Validar monto
        if (montoTexto.isEmpty()) {
            binding.tilMonto.error = "El monto es obligatorio"
            return null
        }

        // toDoubleOrNull() intenta convertir el texto a Double
        // Retorna null si el texto no es un número válido
        val montoNumero = montoTexto.toDoubleOrNull()
        if (montoNumero == null || montoNumero <= 0) {
            binding.tilMonto.error = "Ingresa un monto válido mayor a 0"
            return null
        } else {
            binding.tilMonto.error = null
        }

        // Determinar si es gasto o ingreso según el RadioButton seleccionado
        // rbGasto.isChecked retorna true si el RadioButton de gasto está seleccionado
        val esGasto = binding.rbGasto.isChecked

        // El monto es negativo si es gasto, positivo si es ingreso
        val montoFinal = if (esGasto) -montoNumero else montoNumero

        // Obtener la categoría seleccionada del Spinner
        // selectedItemPosition retorna el índice seleccionado (0, 1, 2, ...)
        // Lo usamos para obtener el valor correspondiente del enum
        val categoriaIndex = binding.spCategoria.selectedItemPosition
        val categoria = Categoria.values()[categoriaIndex]

        // Crear y retornar la transacción
        return Transaccion(
            id = 0,  // el id se asignará después (por la base de datos)
            monto = montoFinal,
            descripcion = descripcion,
            categoria = categoria
        )
    }

    // Devuelve la transacción creada a la Activity que la llamó
    private fun devolverResultado(transaccion: Transaccion) {
        // Crear un Intent para devolver datos
        val resultadoIntent = Intent()
        // putExtra guarda la transacción en el Intent con una clave
        // La transacción debe ser Serializable (lo definimos en el capítulo 4)
        resultadoIntent.putExtra("NUEVA_TRANSACCION", transaccion)

        // setResult indica que la operación fue exitosa y adjunta los datos
        // RESULT_OK es una constante que indica éxito
        setResult(RESULT_OK, resultadoIntent)

        // Cerrar esta Activity y regresar a la anterior
        finish()
    }
}