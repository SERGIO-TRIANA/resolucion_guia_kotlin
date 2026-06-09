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

        // Observar las transacciones para calcular estadísticas
        viewModel.transacciones.observe(viewLifecycleOwner) { lista ->
            mostrarEstadisticas(lista)
        }

        // Observar el resultado de la consulta de tasas
        viewModel.tasasCambio.observe(viewLifecycleOwner) { resultado ->
            // "when" con sealed class: el compilador verifica que cubramos todos los casos
            when (resultado) {
                is ResultadoApi.Cargando -> {
                    // Mostrar el indicador de carga
                    binding.progressTasas.visibility = View.VISIBLE
                    binding.tvTasas.text = ""
                }
                is ResultadoApi.Exito -> {
                    // Ocultar el indicador de carga
                    binding.progressTasas.visibility = View.GONE
                    // Mostrar las tasas relevantes
                    val tasas = resultado.datos.rates
                    val texto = buildString {
                        // buildString construye un String de forma eficiente
                        appendLine("1 USD = ${tasas["COP"] ?: "N/A"} COP")
                        appendLine("1 USD = ${tasas["EUR"] ?: "N/A"} EUR")
                        appendLine("1 USD = ${tasas["GBP"] ?: "N/A"} GBP")
                        appendLine("1 USD = ${tasas["BRL"] ?: "N/A"} BRL")
                        append("1 USD = ${tasas["MXN"] ?: "N/A"} MXN")
                    }
                    binding.tvTasas.text = texto
                }
                is ResultadoApi.Error -> {
                    binding.progressTasas.visibility = View.GONE
                    binding.tvTasas.text = resultado.mensaje
                    binding.tvTasas.setTextColor(
                        requireContext().getColor(R.color.rojo_gasto)
                    )
                }
            }
        }

        // Botón para consultar tasas
        binding.btnConsultarTasas.setOnClickListener {
            viewModel.consultarTasas()
        }
    }

    // Calcula y muestra las estadísticas por categoría
    private fun mostrarEstadisticas(transacciones: List<Transaccion>) {
        // Filtrar solo gastos (monto negativo)
        val gastos = transacciones.filter { !it.esIngreso() }

        // Agrupar por categoría y sumar los montos
        // groupBy agrupa los elementos por una clave (la categoría)
        // mapValues transforma los valores de cada grupo (suma los montos)
        val porCategoria = gastos
            .groupBy { it.categoria }  // Map<Categoria, List<Transaccion>>
            .mapValues { (_, transacciones) ->
                // Sumar los valores absolutos de los montos de cada grupo
                transacciones.sumOf { Math.abs(it.monto) }
            }
            .toList()  // Convertir a lista de pares (Categoria, Double)
            .sortedByDescending { it.second }  // Ordenar de mayor a menor gasto

        // Mostrar las categorías con sus totales
        // buildString construye un String de forma eficiente
        val detalle = buildString {
            for ((categoria, total) in porCategoria) {
                appendLine("${categoria.emoji} ${categoria.etiqueta}: ${formatearMonto(total)}")
            }
        }
        binding.tvDetalleCategorias.text = detalle

        // Mostrar resumen general
        binding.tvTotalTransacciones.text = "Total de transacciones: ${transacciones.size}"

        // Calcular promedio de gastos
        val promedioGasto = if (gastos.isNotEmpty()) {
            gastos.sumOf { Math.abs(it.monto) } / gastos.size
        } else {
            0.0
        }
        binding.tvPromedioGasto.text = "Promedio por gasto: ${formatearMonto(promedioGasto)}"
    }

    private fun formatearMonto(monto: Double): String {
        return "$ ${String.format("%,.0f", monto)}"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}