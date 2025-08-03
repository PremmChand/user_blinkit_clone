package com.example.blinkitclone.fragments

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.blinkitclone.utils.CartListener
import com.example.blinkitclone.utils.Constants
import com.example.blinkitclone.R
import com.example.blinkitclone.utils.Utils
import com.example.blinkitclone.adapters.AdapterBestSeller
import com.example.blinkitclone.adapters.AdapterCategory
import com.example.blinkitclone.adapters.AdapterProduct
import com.example.blinkitclone.databinding.BsSeeAllBinding
import com.example.blinkitclone.databinding.FragmentHomeBinding
import com.example.blinkitclone.databinding.ItemViewProductBinding
import com.example.blinkitclone.models.BestSeller
import com.example.blinkitclone.models.Category
import com.example.blinkitclone.models.Product
import com.example.blinkitclone.roomdb.CartProductTable
import com.example.blinkitclone.viewmodels.UserViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.launch


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val viewModel : UserViewModel by viewModels()
    private lateinit var adapterBestSeller: AdapterBestSeller
    private lateinit var adapterProduct: AdapterProduct
    private var cartListener: CartListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        setAllCategories()
        navigationToSearchFragment()
        onProfileClick()
        fetchBestSeller()
        return binding.root
    }

    private fun fetchBestSeller() {
        binding.shimmerViewContainer.visibility =View.VISIBLE
        lifecycleScope.launch {
            viewModel.fetchProductTypes().collect{
                adapterBestSeller = AdapterBestSeller(::onSeeAllButtonClicked)
                binding.rvBestsellers.adapter = adapterBestSeller
                adapterBestSeller.differ.submitList(it)
                binding.shimmerViewContainer.visibility =View.GONE

            }
        }
    }


    fun onSeeAllButtonClicked(productType : BestSeller){
        val bsSeeAllBinding = BsSeeAllBinding.inflate(LayoutInflater.from(requireContext()))
        val bs  =  BottomSheetDialog(requireContext())
        adapterProduct = AdapterProduct(::onAddButtonClicked, ::onIncrementButtonClicked, ::onDecrementButtonClicked)
        bsSeeAllBinding.rvProducts.adapter = adapterProduct
        adapterProduct.differ.submitList(productType.products)

        bs.setContentView(bsSeeAllBinding.root)

        bs.show()
    }

    fun onAddButtonClicked(product: Product, productBinding: ItemViewProductBinding){
        productBinding.tvAdd.visibility = View.GONE
        productBinding.llProductCount.visibility = View.VISIBLE


        //step 1
        var itemCount = productBinding.tvProductCount.text.toString().toInt()
        itemCount++
        productBinding.tvProductCount.text = itemCount.toString()

        cartListener?.showCartLayout(1)


        //step 2
        product.itemCount = itemCount
        lifecycleScope.launch {
            cartListener?.savingCartItemCount(1)
            saveProductInRoomDB(product)
            viewModel.updateItemCount(product,itemCount)
        }


    }

    private fun saveProductInRoomDB(product: Product) {
        val cartProduct = CartProductTable(
            productId = product.productRandomId!!,
            productTitle = product.productTitle,
            productQuantity = product.productQuantity.toString() + product.productUnit.toString(),
            productPrice = "₹" + "${product.productPrice}",
            productCount =  product.itemCount,
            productStock = product.productStock,
            productImage = product.productImageUris?.get(0)!!,
            productCategory = product.productCategory,
            adminUid = product.adminUid,
            productType = product.productType
        )

        lifecycleScope.launch{
            viewModel.insertCartProduct(cartProduct)
        }
    }

    fun onIncrementButtonClicked(product: Product, productBinding: ItemViewProductBinding){
        var itemCountInc = productBinding.tvProductCount.text.toString().toInt()
        itemCountInc++

        if(product.productStock!! + 1 > itemCountInc){
            productBinding.tvProductCount.text = itemCountInc.toString()

            cartListener?.showCartLayout(1)

            //step 2
            product.itemCount = itemCountInc
            lifecycleScope.launch {
                cartListener?.savingCartItemCount(1)
                saveProductInRoomDB(product)
                viewModel.updateItemCount(product,itemCountInc)
            }
        }else{
            Utils.showToast(requireContext(),"Can't add more items of this")
        }


    }

    fun onDecrementButtonClicked(product: Product, productBinding: ItemViewProductBinding){
        var itemCountDec = productBinding.tvProductCount.text.toString().toInt()
        itemCountDec--

        product.itemCount = itemCountDec
        lifecycleScope.launch {
            cartListener?.savingCartItemCount(-1)
            saveProductInRoomDB(product)
            viewModel.updateItemCount(product,itemCountDec)
        }

        if(itemCountDec > 0) {
            productBinding.tvProductCount.text = itemCountDec.toString()
        }else{
            lifecycleScope.launch {
                viewModel.deleteCartProduct(product.productRandomId!!)
            }
            productBinding.tvAdd.visibility = View.VISIBLE
            productBinding.llProductCount.visibility = View.GONE
            productBinding.tvProductCount.text = "0"
        }

        cartListener?.showCartLayout(-1)

        //step 2

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is CartListener){
            cartListener = context
        }
        else{
            throw ClassCastException("Please implement cart listener")
        }
    }


    private fun onProfileClick() {
        binding.ivProfile.setOnClickListener{
            findNavController().navigate(R.id.action_homeFragment_to_profileFragment)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        statusBarUI()

    }



    private fun navigationToSearchFragment() {
        binding.search.setOnClickListener{
            findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
        }
    }

    private fun setAllCategories() {
        val categoryList = ArrayList<Category>()

        for (i in 0 until Constants.allProductCategoryIcon.size) {
            categoryList.add(Category(Constants.allProductsCategory[i], Constants.allProductCategoryIcon[i]))
        }

        binding.rvCategories.adapter = AdapterCategory(categoryList, ::onCategoryIconClicked)

    }

    fun onCategoryIconClicked(category: Category){
        val bundle  = Bundle()
        bundle.putString("category", category.title)
       findNavController().navigate(R.id.action_homeFragment_to_categoryFragment, bundle)
    }


    private fun statusBarUI(){
        if (Build.VERSION.SDK_INT >= 21) {
            val window: Window = requireActivity().window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor =  ContextCompat.getColor(requireContext(),
                R.color.yellow
            )
        }
    }




}