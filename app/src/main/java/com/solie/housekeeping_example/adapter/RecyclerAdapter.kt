package com.solie.housekeeping_example.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.solie.housekeeping_example.R
import com.solie.housekeeping_example.databinding.ItemRecyclerListBinding
import com.solie.housekeeping_example.item.DBItem

class RecyclerAdapter : RecyclerView.Adapter<RecyclerAdapter.RecyclerViewHolder>() {

    private var data = mutableListOf<DBItem>()
    private lateinit var recyclerClickListener : RecyclerClickListener

    interface RecyclerClickListener {
        fun onClick (view : View, position : Int)
    }

    fun recyclerClickListener (recyclerClickListener : RecyclerClickListener) {
        this.recyclerClickListener = recyclerClickListener
    }

    class RecyclerViewHolder(val binding : ItemRecyclerListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind (dbItem: DBItem) {
            binding.itemDate.text = "${dbItem.Month} / ${dbItem.Date}"
            binding.itemReason.text = dbItem.Reason
            binding.itemCost.text = dbItem.Cost
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ItemRecyclerListBinding>(layoutInflater, R.layout.item_recycler_list, parent, false)

        return RecyclerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        holder.bind(data[position])
        holder.binding.itemDate.text = "${data[position].Month} / ${data[position].Date}"
        holder.binding.itemReason.text = data[position].Reason
        if(data[position].Type == "Increase") {
            holder.binding.itemCost.setTextColor(Color.parseColor("#4CAF50"))
            holder.binding.itemCost.text = "+ ${data[position].Cost}"
        } else {
            holder.binding.itemCost.setTextColor(Color.parseColor("#F44336"))
            holder.binding.itemCost.text = "- ${data[position].Cost}"
        }
        holder.itemView.setOnClickListener {
            recyclerClickListener.onClick(it, position)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun setRecycler (list : MutableList<DBItem>) {
        data = list.toMutableList()
        notifyDataSetChanged()
    }
}