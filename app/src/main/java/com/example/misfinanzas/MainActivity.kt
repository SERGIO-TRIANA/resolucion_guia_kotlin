// MainActivity.kt
// Pantalla principal con persistencia Room
package com.example.misfinanzas

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.example.misfinanzas.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private lateinit var adapter: TransaccionAdapter

    // Callback para recibir resultado de AgregarActivity
    private val lanzarAgregar = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { resultado ->
        if (resultado.resultCode == RESULT_OK) {
            val nueva = resultado.data?.getSerializableExtra("NUEVA_TRANSACCION") as? Transaccion
            if (nueva != null) {
                // Delegar al ViewModel que guarda en Room
                viewModel.agregarTransaccion(nueva)
                Toast.makeText(this, "Transacción guardada", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configurarRecyclerView()
        observarDatos()

        // Botón de agregar
        binding.btnAgregar.setOnClickListener {
            val intent = Intent(this, AgregarActivity::class.java)
            lanzarAgregar.launch(intent)
        }
    }

    private fun configurarRecyclerView() {
        adapter = TransaccionAdapter(emptyList()) { transaccion ->
            Toast.makeText(
                this,
                "${transaccion.descripcion}: ${transaccion.montoFormateado()}",
                Toast.LENGTH_SHORT
            ).show()
        }
        binding.rvTransacciones.layoutManager = LinearLayoutManager(this)
        binding.rvTransacciones.adapter = adapter
    }

    // Observar los LiveData del ViewModel
    // Room actualiza estos LiveData automáticamente cuando la base de datos cambia
    private fun observarDatos() {
        // Observar la lista de transacciones
        viewModel.transacciones.observe(this) { lista ->
            // Recrear el adapter con la nueva lista
            adapter = TransaccionAdapter(lista) { transaccion ->
                Toast.makeText(
                    this,
                    "${transaccion.descripcion}: ${transaccion.montoFormateado()}",
                    Toast.LENGTH_SHORT
                ).show()
            }
            binding.rvTransacciones.adapter = adapter

            // Si la lista está vacía, cargar datos de prueba
            if (lista.isEmpty()) {
                viewModel.insertarDatosDePrueba()
            }
        }

        // Observar el balance
        viewModel.balance.observe(this) { balance ->
            binding.tvBalance.text = formatearMonto(balance)
        }

        // Observar ingresos
        viewModel.ingresos.observe(this) { ingresos ->
            binding.tvIngresos.text = formatearMonto(ingresos)
        }

        // Observar gastos
        viewModel.gastos.observe(this) { gastos ->
            binding.tvGastos.text = formatearMonto(gastos)
        }

        // Observar cantidad
        viewModel.cantidad.observe(this) { cantidad ->
            binding.tvNumTransacciones.text = "$cantidad"
        }
    }

    private fun formatearMonto(monto: Double): String {
        return "$ ${String.format("%,.0f", monto)}"
    }
}