package com.example.misfinanzas

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.misfinanzas.databinding.ItemTransaccionBinding

class TransaccionAdapter(
    private val transacciones: List<Transaccion>,
    private val onClick: (Transaccion) -> Unit
) : RecyclerView.Adapter<TransaccionAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemTransaccionBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTransaccionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = transacciones[position]
        holder.binding.tvDescripcion.text = item.descripcion
        holder.binding.tvCategoria.text = item.categoria.etiqueta
        holder.binding.tvEmoji.text = item.categoria.emoji
        holder.binding.tvMonto.text = item.montoFormateado()
        
        // Color según sea ingreso o gasto
        val color = if (item.esIngreso()) 0xFF2E7D32.toInt() else 0xFFC62828.toInt()
        holder.binding.tvMonto.setTextColor(color)

        holder.binding.root.setOnClickListener { onClick(item) }
    }

    override fun getItemCount(): Int = transacciones.size
}