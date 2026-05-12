// MainActivity.kt
// Pantalla principal usando ViewModel y LiveData
package com.example.misfinanzas

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.activity.result.contract.ActivityResultContracts
// viewModels() es una extensión que crea o recupera el ViewModel
import androidx.activity.viewModels
import com.example.misfinanzas.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // viewModels() crea el ViewModel la primera vez y lo recupera en recreaciones
    // "by viewModels()" es una delegación: la primera vez que se accede, se crea
    // En rotaciones de pantalla, devuelve el MISMO ViewModel (no crea uno nuevo)
    private val viewModel: MainViewModel by viewModels()

    // Adapter como propiedad para poder actualizarlo
    private lateinit var adapter: TransaccionAdapter

    // Callback para recibir el resultado de AgregarActivity
    private val lanzarAgregar = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { resultado ->
        if (resultado.resultCode == RESULT_OK) {
            val nueva = resultado.data?.getSerializableExtra("NUEVA_TRANSACCION") as? Transaccion
            if (nueva != null) {
                // Ahora delegamos al ViewModel en lugar de manejar la lista directamente
                viewModel.agregarTransaccion(nueva)
                // El RecyclerView se actualiza automáticamente gracias al observe
                binding.rvTransacciones.scrollToPosition(0)
                Toast.makeText(this, "Transacción agregada", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurar el RecyclerView (sin datos todavía)
        configurarRecyclerView()

        // Observar los datos del ViewModel
        // Cada observe se ejecuta automáticamente cuando el dato cambia
        observarDatos()

        // Botón de agregar
        binding.btnAgregar.setOnClickListener {
            val intent = Intent(this, AgregarActivity::class.java)
            lanzarAgregar.launch(intent)
        }
    }

    private fun configurarRecyclerView() {
        // Inicialmente con lista vacía, se llenará cuando el observe notifique
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

    // Suscribirse a los LiveData del ViewModel
    private fun observarDatos() {
        // Observar la lista de transacciones
        // Cada vez que la lista cambia, este bloque se ejecuta
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
            // Actualizar el contador
            binding.tvNumTransacciones.text = "${lista.size}"
        }

        // Observar el balance
        viewModel.balance.observe(this) { balance ->
            binding.tvBalance.text = formatearMonto(balance)
        }

        // Observar los ingresos
        viewModel.ingresos.observe(this) { ingresos ->
            binding.tvIngresos.text = formatearMonto(ingresos)
        }

        // Observar los gastos
        viewModel.gastos.observe(this) { gastos ->
            binding.tvGastos.text = formatearMonto(gastos)
        }
    }

    private fun formatearMonto(monto: Double): String {
        return "$ ${String.format("%,.0f", monto)}"
    }
}