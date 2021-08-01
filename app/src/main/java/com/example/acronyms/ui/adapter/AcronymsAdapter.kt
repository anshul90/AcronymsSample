package com.example.acronyms.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.acronyms.R
import com.example.acronyms.databinding.ItemLayoutBinding
import java.util.*
import kotlin.collections.ArrayList

class AcronymsAdapter(private val acronyms : ArrayList<String>) :
    RecyclerView.Adapter<AcronymsAdapter.AcronymsViewHolder>() {

    override fun onCreateViewHolder(viewGroup : ViewGroup, viewType : Int) : AcronymsViewHolder {
        val acronymsItemBinding = DataBindingUtil.inflate<ItemLayoutBinding>(
            LayoutInflater.from(viewGroup.context),
            R.layout.item_layout, viewGroup, false
        )

        return AcronymsViewHolder(acronymsItemBinding)
    }

    override fun getItemCount() : Int = acronyms.size

    override fun onBindViewHolder(holder : AcronymsViewHolder, position : Int) {
        holder.acronymsItemBinding.textViewAcronyms.text = acronyms[position].capitalize(Locale.ROOT)
    }

    fun addAcronym(acronymsResponse : List<String>) {
        this.acronyms.apply {
            clear()
            addAll(acronymsResponse)
        }
    }

    inner class AcronymsViewHolder(var acronymsItemBinding : ItemLayoutBinding) :
        RecyclerView.ViewHolder(acronymsItemBinding.root)
}
