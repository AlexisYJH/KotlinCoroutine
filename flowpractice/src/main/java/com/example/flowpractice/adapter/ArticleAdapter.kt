package com.example.flowpractice.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.flowpractice.databinding.ItemArticleBinding
import com.example.flowpractice.model.Article

/**
 * @author AlexisYin
 */
class ArticleAdapter() : RecyclerView.Adapter<BindingViewHolder>() {
    private val data = ArrayList<Article>();
    fun setData(data : List<Article>) {
        this.data.clear()
        this.data.addAll(data)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder {
        val binding = ItemArticleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BindingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BindingViewHolder, position: Int) {
        val item =  data[position]
        val binding = holder.binding as ItemArticleBinding
        binding.text.text = item.text
    }

    override fun getItemCount() = data.size
}