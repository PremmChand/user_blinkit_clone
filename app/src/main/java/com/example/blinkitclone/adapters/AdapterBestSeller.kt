package com.example.blinkitclone.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.blinkitclone.databinding.ItemViewBestsellerBinding
import com.example.blinkitclone.models.BestSeller

class AdapterBestSeller(val onSeeAllButtonClicked: (BestSeller) -> Unit) : RecyclerView.Adapter<AdapterBestSeller.BestSellerViewHolder>(){
    class  BestSellerViewHolder(val binding : ItemViewBestsellerBinding) :ViewHolder(binding.root)

    private val diffUtil = object: DiffUtil.ItemCallback<BestSeller>(){
        override fun areItemsTheSame(oldItem: BestSeller, newItem: BestSeller): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: BestSeller, newItem: BestSeller): Boolean {
            return oldItem == newItem
        }

    }
    val differ = AsyncListDiffer(this, diffUtil)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdapterBestSeller.BestSellerViewHolder {
        return BestSellerViewHolder(ItemViewBestsellerBinding.inflate(LayoutInflater.from(parent.context),parent, false))
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: AdapterBestSeller.BestSellerViewHolder, position: Int) {

        val productType = differ.currentList[position]
        val context = holder.itemView.context

        holder.binding.apply {
            tvProductType.text = productType.productType
            tvTotalProducts.text = "${productType.products?.size ?: 0} products"

            val imageViews = listOf(ivProduct1, ivProduct2, ivProduct3)
            imageViews.forEach { it.visibility = View.GONE } // reset

            productType.products?.let { products ->
                val displayCount = minOf(imageViews.size, products.size)

                for (i in 0 until displayCount) {
                    imageViews[i].visibility = View.VISIBLE
                    val imageUrl = products[i].productImageUris?.firstOrNull()
                    imageUrl?.let {
                        Glide.with(context)
                            .load(it)
                            .into(imageViews[i])
                    }
                }

                if (products.size > 3) {
                    tvProductCount.visibility = View.VISIBLE
                    tvProductCount.text = "+${products.size - 3}"
                } else {
                    tvProductCount.visibility = View.GONE
                }
            }
        }

       holder.itemView.setOnClickListener{ onSeeAllButtonClicked(productType) }


    }


}