// EstadisticasFragment.kt
// Fragment que muestra estadísticas de gastos por categoría
package com.example.misfinanzas

import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import android.os.Bundle
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.misfinanzas.api.ResultadoApi
import com.example.misfinanzas.databinding.FragmentEstadisticasBinding

class EstadisticasFragment : Fragment() {

    private var _binding: FragmentEstadisticasBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEstadisticasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            // Observar las transacciones para calcular estadísticas
            viewModel.transacciones.observe(viewLifecycleOwner) { lista ->
                mostrarEstadisticas(lista)
            }

            // Observar el resultado de la consulta de tasas
            viewModel.tasasCambio.observe(viewLifecycleOwner) { resultado ->
                when (resultado) {
                    is ResultadoApi.Cargando -> {
                        progressTasas.mostrar()
                        tvTasas.text = ""
                    }
                    is ResultadoApi.Exito -> {
                        progressTasas.ocultar()
                        val tasas = resultado.datos.rates
                        tvTasas.text = buildString {
                            appendLine("1 USD = ${tasas["COP"] ?: "N/A"} COP")
                            appendLine("1 USD = ${tasas["EUR"] ?: "N/A"} EUR")
                            appendLine("1 USD = ${tasas["GBP"] ?: "N/A"} GBP")
                            appendLine("1 USD = ${tasas["BRL"] ?: "N/A"} BRL")
                            append("1 USD = ${tasas["MXN"] ?: "N/A"} MXN")
                        }
                    }
                    is ResultadoApi.Error -> {
                        progressTasas.ocultar()
                        tvTasas.text = resultado.mensaje
                        tvTasas.setTextColor(requireContext().getColor(R.color.rojo_gasto))
                    }
                }
            }

            btnConsultarTasas.setOnClickListener {
                viewModel.consultarTasas()
            }
        }
    }

    // Calcula y muestra las estadísticas usando técnicas avanzadas de Kotlin
    private fun mostrarEstadisticas(transacciones: List<Transaccion>) {
        // Top 3 categorías de gasto usando encadenamiento funcional
        val porCategoria = transacciones
            .filter { !it.esIngreso() }
            .groupBy { it.categoria }
            .mapValues { (_, lista) -> lista.sumOf { Math.abs(it.monto) } }
            .toList()
            .sortedByDescending { it.second }

        with(binding) {
            // Mostrar las categorías con sus totales
            tvDetalleCategorias.text = buildString {
                porCategoria.forEach { (categoria, total) ->
                    appendLine("${categoria.emoji} ${categoria.etiqueta}: ${total.formatearCOP()}")
                }
            }

            // Construir el texto del resumen usando buildString y operaciones funcionales
            tvTotalTransacciones.text = "Total de transacciones: ${transacciones.size}"

            // Calcular promedio de gastos usando let y takeIf para manejar lista vacía
            transacciones
                .filter { !it.esIngreso() }
                .takeIf { it.isNotEmpty() }
                ?.let { gastos ->
                    val promedio = gastos.sumOf { Math.abs(it.monto) } / gastos.size
                    tvPromedioGasto.text = "Promedio por gasto: ${promedio.formatearCOP()}"
                    tvPromedioGasto.mostrar()
                } ?: run {
                tvPromedioGasto.ocultar()
            }

            // Información extra opcional usando técnicas avanzadas
            val resumenExtra = buildString {
                // Mayor ingreso
                transacciones
                    .filter { it.esIngreso() }
                    .maxByOrNull { it.monto }
                    ?.let { mayor ->
                        appendLine("\nMayor ingreso: ${mayor.descripcion} (${mayor.montoFormateado()})")
                    }

                // Mayor gasto
                transacciones
                    .filter { !it.esIngreso() }
                    .minByOrNull { it.monto }
                    ?.let { mayor ->
                        append("Mayor gasto: ${mayor.descripcion} (${mayor.montoFormateado()})")
                    }
            }

            if (resumenExtra.isNotEmpty()) {
                tvTotalTransacciones.append(resumenExtra)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}