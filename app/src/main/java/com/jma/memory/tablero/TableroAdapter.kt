package com.jma.memory.tablero

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jma.memory.R
import com.jma.memory.databinding.LayoutCeldaBinding

class TableroAdapter(val tablero: Tablero) : RecyclerView.Adapter<TableroAdapter.TableroHolder>() {

    private var onItemClickListener: (Int) -> Unit = {}

    fun setOnItemClickListener(listener: (Int)->Unit) {
        this.onItemClickListener = listener
    }



    inner class TableroHolder(val bind: LayoutCeldaBinding) : RecyclerView.ViewHolder(bind.root) {
        fun bindItem(celda: Celda?) {
            Glide.with(bind.root).load(if(celda?.visible == true) celda?.img else R.drawable.memory_placeholder)
                .placeholder(R.drawable.memory_placeholder).into(bind.imagen)
            bind.imagen.setOnClickListener { onItemClickListener(adapterPosition) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TableroHolder {
        val inflador = LayoutInflater.from(parent.context);
        val binding = LayoutCeldaBinding.inflate(inflador,parent,false);
        return TableroHolder(binding);
    }

    override fun onBindViewHolder(holder: TableroHolder, position: Int) {
        return holder.bindItem(tablero.celda(position))
    }

    override fun getItemCount(): Int {
        return tablero.tamaño()
    }
}