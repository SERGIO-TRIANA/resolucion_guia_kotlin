// MainActivity.kt
// Activity principal que maneja la navegación entre Fragments
package com.example.misfinanzas

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
// Fragment es la clase base
import androidx.fragment.app.Fragment
import androidx.activity.viewModels
import com.example.misfinanzas.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    // ViewModel compartido entre la Activity y todos los Fragments
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Cargar el Fragment inicial solo si es la primera vez
        // savedInstanceState es null la primera vez, pero tiene datos si se recreó
        // Sin esta verificación, al rotar se duplicaría el Fragment
        if (savedInstanceState == null) {
            cargarFragment(InicioFragment())
        }

        // Cargar datos de prueba si la base de datos está vacía
        viewModel.transacciones.observe(this) { lista ->
            if (lista.isEmpty()) {
                viewModel.insertarDatosDePrueba()
            }
        }

        // Configurar la navegación inferior
        // setOnItemSelectedListener se ejecuta cuando el usuario toca una pestaña
        binding.bottomNav.setOnItemSelectedListener { item ->
            // item.itemId es el id definido en bottom_menu.xml
            when (item.itemId) {
                R.id.nav_inicio -> cargarFragment(InicioFragment())
                R.id.nav_transacciones -> cargarFragment(TransaccionesFragment())
                R.id.nav_estadisticas -> cargarFragment(EstadisticasFragment())
            }
            // Retornar true indica que el item fue manejado
            true
        }
    }

    // Reemplaza el Fragment actual por uno nuevo
    private fun cargarFragment(fragment: Fragment) {
        // supportFragmentManager maneja los Fragments de esta Activity
        // beginTransaction inicia una transacción (un conjunto de cambios)
        supportFragmentManager.beginTransaction()
            // replace reemplaza el contenido del contenedor por el nuevo Fragment
            .replace(R.id.fragmentContainer, fragment)
            // commit ejecuta la transacción
            .commit()
    }
}