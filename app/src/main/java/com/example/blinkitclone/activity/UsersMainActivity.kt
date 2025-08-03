package com.example.blinkitclone.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.blinkitclone.utils.CartListener
import com.example.blinkitclone.adapters.AdapterCartProducts
import com.example.blinkitclone.databinding.ActivityUsersMainBinding
import com.example.blinkitclone.databinding.BsCartProductsBinding
import com.example.blinkitclone.roomdb.CartProductTable
import com.example.blinkitclone.viewmodels.UserViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog


class UsersMainActivity : AppCompatActivity(), CartListener {
    private lateinit var binding: ActivityUsersMainBinding
    private val viewModel : UserViewModel by viewModels()
    private lateinit var cartProductList : List<CartProductTable>
   private lateinit var adapterCartProducts: AdapterCartProducts
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        permission()

        binding = ActivityUsersMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getAllCartProducts()
        getTotalItemCountInCart()
        onCartClicked()
        onNextButtonClicked()
    }

    fun permission(){
        // Request notification permission on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    100 // Request code; can be any integer
                )
            }
        }

    }

    private fun onNextButtonClicked(){
        binding.btnNext.setOnClickListener{
            startActivity(Intent(this, OrderPlaceActivity::class.java))
        }
    }


    private  fun getAllCartProducts(){
        viewModel.getAll().observe(this) {
                cartProductList  = it
        }
    }
    private fun getTotalItemCountInCart(){
        viewModel.fetchTotalCartItemsCount().observe(this){
          if(it > 0){
              binding.llCart.visibility = View.VISIBLE
              binding.tvNumberOfProductCount.text = it.toString()
          }
          else{
              binding.llCart.visibility = View.GONE

          }
        }
    }

    override fun showCartLayout(itemCount:Int) {
        val previousCount = binding.tvNumberOfProductCount.text.toString().toInt()
        val updatedCount = previousCount + itemCount

        if(updatedCount > 0){
            binding.llCart.visibility = View.VISIBLE
            binding.tvNumberOfProductCount.text = updatedCount.toString()
        }
        else{
            binding.llCart.visibility = View.GONE
            binding.tvNumberOfProductCount.text = "0"
        }
    }

    override fun savingCartItemCount(itemCount: Int) {
        viewModel.fetchTotalCartItemsCount().observe(this){
            viewModel.savingCartItemCount(it + itemCount)
        }

    }

    override fun onCartClicked() {
        binding.llItemCart.setOnClickListener{
        val bsCartProductBinding = BsCartProductsBinding.inflate(LayoutInflater.from(this))

            val bs = BottomSheetDialog(this)
            bs.setContentView(bsCartProductBinding.root)

            bsCartProductBinding.tvNumberOfProductCount.text = binding.tvNumberOfProductCount.text

            bsCartProductBinding.btnNext.setOnClickListener{
                startActivity(Intent(this, OrderPlaceActivity::class.java))
            }

            adapterCartProducts = AdapterCartProducts()
            bsCartProductBinding.rvProductsItems.adapter = adapterCartProducts
            adapterCartProducts.differ.submitList(cartProductList)

            bs.show()
        }
    }

    override fun hideCartLayout() {
        binding.llCart.visibility = View.GONE
        binding.tvNumberOfProductCount.text = "0"
    }
}