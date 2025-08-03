package com.example.blinkitclone.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.blinkitclone.utils.Utils
import com.example.blinkitclone.adapters.AdapterCartProducts
import com.example.blinkitclone.databinding.ActivityOrderPlaceBinding
import com.example.blinkitclone.databinding.AddressLayoutBinding
import com.example.blinkitclone.viewmodels.UserViewModel
import com.razorpay.Checkout
import com.example.blinkitclone.BuildConfig
import com.example.blinkitclone.utils.CartListener
import com.example.blinkitclone.models.Orders
import com.razorpay.PaymentResultListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class OrderPlaceActivity : AppCompatActivity(), PaymentResultListener {
    private  lateinit var binding: ActivityOrderPlaceBinding
    private val viewModel : UserViewModel by viewModels()
    private lateinit var adapterCartProducts: AdapterCartProducts
    private val razorpayKey = BuildConfig.RAZORPAY_SECRET_KEY
    private var cartListener: CartListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderPlaceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        backToUserMainActivity()
        getAllCartProducts()
        onPlaceOrderClicked()
       // startRazorpayPayment()
    }

    private fun initializePhonePay() {
      //  PhonePe.init(this, PhonePeEnvironment.SANDBOX, String merchantId, String appId)

    }

    private fun onPlaceOrderClicked() {
        binding.btnNext.setOnClickListener{
            viewModel.getAddressStatus().observe(this){ status ->
                if(status){
                    //Payment work
                    startRazorpayPayment()
                }else{
                    val addressLayoutBinding = AddressLayoutBinding.inflate(LayoutInflater.from(this))
                    val alertDialog = AlertDialog.Builder(this)
                        .setView(addressLayoutBinding.root)
                        .create()
                    alertDialog.show()

                    addressLayoutBinding.btnAdd.setOnClickListener{
                        lifecycleScope.launch {
                            saveAddress(alertDialog, addressLayoutBinding)
                        }

                    }
                }
            }
        }
    }




    private fun startRazorpayPayment() {
        val checkout = Checkout()
        checkout.setKeyID(razorpayKey) // Replace with your test key
        // Disable the SDK compatibility status screen
        checkout.setFullScreenDisable(true)

        val totalAmount = binding.tvGrandTotal.text.toString().toInt() * 100

        try {
            val options = JSONObject()
            options.put("name", "Blinkit Clone")
            options.put("description", "Order Payment")
            options.put("currency", "INR")
            options.put("amount", totalAmount)

            val prefill = JSONObject()
            prefill.put("email", "test@example.com")
            prefill.put("contact", "9876543210")
            options.put("prefill", prefill)

            checkout.open(this, options)
        } catch (e: Exception) {
            Toast.makeText(this, "Payment error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onPaymentSuccess(razorpayPaymentID: String?) {
        Utils.showToast(this, "Payment Success: $razorpayPaymentID")
        saveOrder()
        viewModel.deleteCartProducts()
        viewModel.savingCartItemCount(0)
        cartListener?.hideCartLayout()
        Utils.hideDialog()
        startActivity(Intent(this, UsersMainActivity::class.java))
        finish()
    }

    override fun onPaymentError(errorCode: Int, response: String?) {
        Toast.makeText(this, "Payment Failed: $response", Toast.LENGTH_SHORT).show()
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun saveOrder() {
        viewModel.getAll().observe(this){cartProductsList ->
            if(cartProductsList.isNotEmpty()){
                viewModel.getUserAddress { address ->
                    val order = Orders(
                        orderId =  Utils.getRandomId(), orderList = cartProductsList,
                        userAddress = address, orderStatus = 0, orderDate = Utils.getCurrentDate(),
                        orderingUserId = Utils.getCurrentUserId()
                    )
                    viewModel.saveOrderedProducts(order)
                    // Notification
                    // Send notification on IO dispatcher
                    lifecycleScope.launch(Dispatchers.IO) {
                        viewModel.sendNotification(
                            cartProductsList[0].adminUid!!,
                            "Ordered",
                            "Some products have been ordered",
                            this@OrderPlaceActivity
                        )
                    }

                }
                for (products in cartProductsList){
                    val count = products.productCount
                    val stock = products.productStock?.minus(count!!)
                    if (stock != null) {
                        viewModel.saveProductsAfterOrder(stock, products)
                    }
                }

            }
        }
    }



    private suspend fun saveAddress(alertDialog: AlertDialog, addressLayoutBinding: AddressLayoutBinding) {
        Utils.showDialog(this,"Processing...")

        val userPinCode = addressLayoutBinding.etPinCode.text.toString()
        val userPhoneNumber = addressLayoutBinding.etPhoneNumber.text.toString()
        val userState = addressLayoutBinding.etState.text.toString()
        val userDistrict = addressLayoutBinding.etDistrict.text.toString()
        val userAddress = addressLayoutBinding.etDesriptiveAddress.text.toString()

        val address = "$userPinCode, $userDistrict($userState), $userAddress, $userPhoneNumber"

        lifecycleScope.launch {
            viewModel.saveUserAddress(address)
            viewModel.saveAddressStatus()
        }

        withContext(Dispatchers.Main){
            alertDialog.dismiss()
            Utils.hideDialog()
            Utils.showToast(this@OrderPlaceActivity,"Address saved")
            startRazorpayPayment()
        }

    }

    private fun backToUserMainActivity() {
        binding.tbOrderPlaceActivity.setNavigationOnClickListener{
            startActivity(Intent(this, UsersMainActivity::class.java))
            finish()
        }
    }

    private fun getAllCartProducts() {
        viewModel.getAll().observe(this) {cartProductList ->

            adapterCartProducts = AdapterCartProducts()
            binding.rvProductsItems.adapter = adapterCartProducts
            adapterCartProducts.differ.submitList(cartProductList)

            var totalPrice = 0

            for(products in cartProductList){
                val price = products.productPrice!!.substring(1)?.toInt()
                val itemCount = products.productCount!!
                totalPrice += (price?.times(itemCount)!!)
            }

            binding.tvSubTotal.text = totalPrice.toString()

            if(totalPrice < 200){
                binding.tvDeliveryCharge.text = "₹15"
                totalPrice += 15
            }
            binding.tvGrandTotal.text = totalPrice.toString()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        Checkout.clearUserData(this)
    }


}