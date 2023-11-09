package com.dongquan.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.baidu.mapapi.search.sug.SuggestionResult
import com.baidu.mapapi.search.sug.SuggestionResult.SuggestionInfo
import com.example.sensor.R
import com.example.sensor.databinding.AdapterSearchItemBinding

class SearchResultAdapter: RecyclerView.Adapter<SearchResultAdapter.SearchResultVH>(){
    private var list = mutableListOf<SuggestionInfo>()
    private var listener: OnItemOnclickListener? = null
    inner class SearchResultVH(parent: ViewGroup, val binding: AdapterSearchItemBinding = AdapterSearchItemBinding.inflate(
        LayoutInflater.from(parent.context), parent, false
    )) :RecyclerView.ViewHolder(binding.root)

    fun setData(data: MutableList<SuggestionResult.SuggestionInfo>){
        this.list = data
        notifyDataSetChanged()
    }

    fun clearData() {
        this.list.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultVH {
        return SearchResultVH(parent)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: SearchResultVH, position: Int) {
        holder.binding.key.text = list[position].key
        holder.binding.city.text = list[position].city + list[position].district
        holder.itemView.setOnClickListener {
            listener?.onItemOnclick(list[position])
        }
    }

    fun setOnItemOnclickListener(onItemOnclickListener: OnItemOnclickListener){
        listener = onItemOnclickListener
    }
    interface OnItemOnclickListener{
        fun onItemOnclick(info: SuggestionInfo)
    }
}