package com.example.blinkitclone.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.blinkitclone.R
import com.example.blinkitclone.adapters.AdapterCartProducts
import com.example.blinkitclone.adapters.AdapterOrders
import com.example.blinkitclone.databinding.FragmentOrderDetailBinding
import com.example.blinkitclone.viewmodels.UserViewModel
import kotlinx.coroutines.launch

class OrderDetailFragment : Fragment() {
    lateinit var binding: FragmentOrderDetailBinding
    private val  viewModel : UserViewModel by viewModels()
    private lateinit var adapterCartProducts : AdapterCartProducts
    private var status = 0
    private var orderId = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrderDetailBinding.inflate(layoutInflater)

        getValues()
        settingStatus()
        onBackButtonClicked()
        lifecycleScope.launch{ getOrderedProducts() }

        return binding.root
    }

    private fun onBackButtonClicked() {
        binding.tbOderDetailFragment.setNavigationOnClickListener{
            findNavController().navigate(R.id.action_orderDetailFragment_to_ordersFragment)
        }
    }

    private suspend fun getOrderedProducts() {
        viewModel.getOrderedProducts(orderId).collect{ cartList ->
            adapterCartProducts  = AdapterCartProducts()
            binding.rvProductItems.adapter = adapterCartProducts
            adapterCartProducts.differ.submitList(cartList)
        }
    }

    private fun settingStatus() {

        val views = listOf(
            binding.iv1,
            binding.iv2,
            binding.view1,
            binding.iv3,
            binding.view2,
            binding.iv4,
            binding.view3
        )

        val indicesToEnable = when (status) {
            0 -> 0..0
            1 -> 0..2
            2 -> 0..4
            3 -> 0..6
            else -> -1..-1
        }

        val blueColor = ContextCompat.getColorStateList(requireContext(), R.color.blue)

        for (i in indicesToEnable) {
            views[i].backgroundTintList = blueColor
        }

    }

    private fun getValues() {
    val bundle = arguments
       status =  bundle?.getInt("status")!!
       orderId =  bundle?.getString("orderId").toString()

        Log.d("orderedItemsID in OrderDetailFragment",status.toString())
        Log.d("orderedItemsID in OrderDetailFragment",orderId )
    }



}