package com.example.blinkitclone.fragments

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsetsController
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.blinkitclone.R
import com.example.blinkitclone.activity.UsersMainActivity
import com.example.blinkitclone.databinding.FragmentSplashBinding
import com.example.blinkitclone.viewmodels.AuthViewModel
import kotlinx.coroutines.launch


class SplashFragment : Fragment() {
    private lateinit var binding: FragmentSplashBinding
    private val viewModel : AuthViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =FragmentSplashBinding.inflate(layoutInflater)
        Handler(Looper.getMainLooper()).postDelayed({
            lifecycleScope.launch {
                viewModel.isACurrentUser.collect{  isLoggedIn ->
                if (isLoggedIn){
                        startActivity(Intent(requireContext(), UsersMainActivity::class.java))
                        requireActivity().finish()
                    }else{
                       // findNavController().navigate(R.id.action_splashFragment_to_signInFragment)
                    findNavController().navigate(
                        R.id.action_splashFragment_to_signInFragment, null,
                        NavOptions.Builder().setPopUpTo(R.id.splashFragment,true)
                            .build())
                    }
                }
            }

        },3000)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setStatusBarColor()
    }

    @Suppress("DEPRECATION")
    private fun setStatusBarColor() {
        activity?.window?.apply {
            statusBarColor = ContextCompat.getColor(requireContext(), R.color.yellow)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                insetsController?.setSystemBarsAppearance(
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                )
            } else {
                decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }

    /*private fun setStatusBarColor(){
        activity?.window?.apply {
            val statusBarColors = ContextCompat.getColor(requireContext(),R.color.yellow)
            statusBarColor  = statusBarColors
            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.M){
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }*/





}