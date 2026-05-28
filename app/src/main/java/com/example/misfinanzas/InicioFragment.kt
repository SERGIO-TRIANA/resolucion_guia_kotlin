// InicioFragment.kt
// Fragment que muestra el resumen de balance y las últimas transacciones
package com.example.misfinanzas

// Fragment es la clase base para todos los fragments
import androidx.fragment.app.Fragment
// LayoutInflater convierte XML en objetos View
import android.view.LayoutInflater
// ViewGroup es el contenedor padre
import android.view.ViewGroup
import android.view.View
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
// activityViewModels() comparte el ViewModel con la Activity padre
// Así todos los fragments acceden a los mismos datos
import androidx.fragment.app.activityViewModels
import com.example.misfinanzas.databinding.FragmentInicioBinding

class InicioFragment : Fragment() {

    // En Fragments, el binding se maneja diferente que en Activities
    // _binding es nullable porque el Fragment puede existir sin vista
    // (entre onDestroyView y onDestroy)
    private var _binding: FragmentInicioBinding? = null
    // binding es un atajo non-null que solo se usa cuando la vista existe
    // "get() = _binding!!" significa que lanza excepción si _binding es null
    private val binding get() = _binding!!

    // activityViewModels() comparte el ViewModel con la Activity
    // Todos los fragments ven los mismos datos
    private val viewModel: MainViewModel by activityViewModels()

    // onCreateView infla el layout del Fragment
    // Es el equivalente a setContentView en una Activity
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflar el layout del fragment
        _binding = FragmentInicioBinding.inflate(inflater, container, false)
        // Retornar la vista raíz
        return binding.root
    }

    // onViewCreated se llama después de que la vista fue creada
    // Aquí configuramos listeners y observadores
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Observar los datos del ViewModel compartido
        // viewLifecycleOwner es el lifecycle del Fragment (no de la Activity)
        // Usar viewLifecycleOwner en lugar de "this" para evitar memory leaks
        viewModel.balance.observe(viewLifecycleOwner) { balance ->
            binding.tvBalance.text = formatearMonto(balance)
        }

        viewModel.ingresos.observe(viewLifecycleOwner) { ingresos ->
            binding.tvIngresos.text = formatearMonto(ingresos)
        }

        viewModel.gastos.observe(viewLifecycleOwner) { gastos ->
            binding.tvGastos.text = formatearMonto(gastos)
        }

        // Mostrar las últimas 5 transacciones
        viewModel.transacciones.observe(viewLifecycleOwner) { lista ->
            // take(5) obtiene solo los primeros 5 elementos
            val ultimas = lista.take(5)
            val adapter = TransaccionAdapter(ultimas) { /* click */ }
            binding.rvUltimas.layoutManager = LinearLayoutManager(requireContext())
            binding.rvUltimas.adapter = adapter
        }
    }

    // onDestroyView se llama cuando la vista del Fragment se destruye
    // IMPORTANTE: limpiar _binding para evitar memory leaks
    // El Fragment puede seguir existiendo sin vista (en el back stack)
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null  // evitar memory leaks
    }

    private fun formatearMonto(monto: Double): String {
        return "$ ${String.format("%,.0f", monto)}"
    }
}