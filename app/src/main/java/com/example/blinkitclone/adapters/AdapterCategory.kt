package com.example.blinkitclone.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.blinkitclone.databinding.ItemViewProductCategoryBinding
import com.example.blinkitclone.models.Category

class AdapterCategory(
    private val categoryList: List<Category>,
    val onCategoryIconClicked: (Category) -> Unit
) : RecyclerView.Adapter<AdapterCategory.CategoryViewHolder>() {
   inner class CategoryViewHolder(val binding: ItemViewProductCategoryBinding) : ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return CategoryViewHolder(
            ItemViewProductCategoryBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categoryList[position]
       with(holder.binding) {
            ivCategoryImage.setImageResource(category.image)
            tvCategoryTitle.text = category.title
        }
        holder.itemView.setOnClickListener{
            onCategoryIconClicked(category)
        }
    }

}