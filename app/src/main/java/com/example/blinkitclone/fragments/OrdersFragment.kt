package com.example.blinkitclone.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.blinkitclone.R
import com.example.blinkitclone.adapters.AdapterOrders
import com.example.blinkitclone.databinding.FragmentOrdersBinding
import com.example.blinkitclone.models.OrderedItems
import com.example.blinkitclone.viewmodels.UserViewModel
import kotlinx.coroutines.launch


class OrdersFragment : Fragment() {

    lateinit var binding:FragmentOrdersBinding
    private val  viewModel : UserViewModel by viewModels()
    private lateinit var adapterOders : AdapterOrders
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrdersBinding.inflate(layoutInflater)

        onBackButtonClicked()
        getAllOrders()
        return binding.root
    }

    private fun getAllOrders() {
        binding.shimmerViewContainer.visibility = View.VISIBLE

        adapterOders = AdapterOrders(requireContext(), ::orderItemViewClicked)
        binding.rvOrders.adapter = adapterOders

        lifecycleScope.launch {
            viewModel.getAllOrders().collect{orderList ->
                if(orderList.isNotEmpty()){
                    val orderedList =  ArrayList<OrderedItems>()
                    for(orders in orderList){
                        val title = StringBuilder()
                        var totalPrice = 0

                        for(products in orders.orderList!!){
                            val price = products.productPrice!!.substring(1)?.toInt()
                            val itemCount = products.productCount!!
                            totalPrice += (price?.times(itemCount)!!)

                            title.append("${products.productCategory}")
                        }

                        val orderItems = OrderedItems(
                            orders.orderId,
                            orders.orderDate,
                            orders.orderStatus,
                            title.toString(),
                            totalPrice
                        )
                        orderedList.add(orderItems)
                    }

                  // adapterOders = AdapterOrders(requireContext(), ::orderItemViewClicked) //No adapter attached; skipping layout so initialized early
                  //  binding.rvOrders.adapter = adapterOders

                    adapterOders.differ.submitList(orderedList)
                    binding.shimmerViewContainer.visibility = View.GONE
                }
            }
        }
    }

    fun orderItemViewClicked(orderedItems: OrderedItems){
        val bundle = Bundle()
        bundle.putInt("status", orderedItems.itemStatus!!)
        bundle.putString("orderId", orderedItems.orderId!!)
         Log.d("orderedItemsID in OrdersFragment", orderedItems.itemStatus!!.toString())
         Log.d("orderedItemsID in OrdersFragment", orderedItems.orderId!!)
        findNavController().navigate(R.id.action_ordersFragment_to_orderDetailFragment, bundle)
    }
    private fun onBackButtonClicked() {
        binding.tbProfileFragment.setNavigationOnClickListener{
            findNavController().navigate(R.id.action_ordersFragment_to_profileFragment)
        }
    }



}