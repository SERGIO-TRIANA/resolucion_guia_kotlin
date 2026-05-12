// MainActivity.kt
// Pantalla principal con navegación a AgregarActivity
package com.example.misfinanzas

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
// ActivityResultContracts permite registrar callbacks para resultados de Activities
import androidx.activity.result.contract.ActivityResultContracts
import com.example.misfinanzas.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    // Lista mutable de transacciones (mutable para poder agregar nuevas)
    private val transacciones = Transaccion.datosDePrueba().toMutableList()
    private lateinit var adapter: TransaccionAdapter

    // registerForActivityResult registra un callback que se ejecuta
    // cuando la Activity lanzada devuelve un resultado
    // Esto reemplaza al antiguo onActivityResult (que está deprecado)
    private val lanzarAgregar = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { resultado ->
        // Este bloque se ejecuta cuando AgregarActivity termina
        // resultado.resultCode indica si fue exitoso o cancelado
        if (resultado.resultCode == RESULT_OK) {
            // Obtener la transacción del Intent de resultado
            // getSerializableExtra obtiene un objeto Serializable por su clave
            // "as?" hace un cast seguro (retorna null si el tipo no coincide)
            val nueva = resultado.data?.getSerializableExtra("NUEVA_TRANSACCION") as? Transaccion
            if (nueva != null) {
                // Agregar la nueva transacción al inicio de la lista
                transacciones.add(0, nueva)
                // Notificar al adapter que se insertó un item en la posición 0
                adapter.notifyItemInserted(0)
                // Hacer scroll al inicio para ver la nueva transacción
                binding.rvTransacciones.scrollToPosition(0)
                // Recalcular los totales
                actualizarBalance()
                // Mostrar confirmación
                Toast.makeText(this, "Transacción agregada", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configurarRecyclerView()
        actualizarBalance()

        // Botón de agregar: abre AgregarActivity
        binding.btnAgregar.setOnClickListener {
            // Crear un Intent para abrir AgregarActivity
            // "this" = contexto actual, AgregarActivity::class.java = destino
            val intent = Intent(this, AgregarActivity::class.java)
            // Lanzar la Activity y esperar resultado
            lanzarAgregar.launch(intent)
        }
    }

    private fun configurarRecyclerView() {
        adapter = TransaccionAdapter(transacciones) { transaccion ->
            Toast.makeText(
                this,
                "${transaccion.descripcion}: ${transaccion.montoFormateado()}",
                Toast.LENGTH_SHORT
            ).show()
        }
        binding.rvTransacciones.layoutManager = LinearLayoutManager(this)
        binding.rvTransacciones.adapter = adapter
    }

    private fun actualizarBalance() {
        val ingresos = transacciones.filter { it.esIngreso() }.sumOf { it.monto }
        val gastos = transacciones.filter { !it.esIngreso() }.sumOf { Math.abs(it.monto) }
        val balance = transacciones.sumOf { it.monto }

        binding.tvBalance.text = formatearMonto(balance)
        binding.tvIngresos.text = formatearMonto(ingresos)
        binding.tvGastos.text = formatearMonto(gastos)
        binding.tvNumTransacciones.text = "${transacciones.size}"
    }

    private fun formatearMonto(monto: Double): String {
        return "$ ${String.format("%,.0f", monto)}"
    }
}