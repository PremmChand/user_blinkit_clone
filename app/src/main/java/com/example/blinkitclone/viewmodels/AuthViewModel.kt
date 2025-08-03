package com.example.blinkitclone.viewmodels

import android.app.Activity
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.blinkitclone.utils.Utils
import com.example.blinkitclone.models.Users
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import java.util.concurrent.TimeUnit

class AuthViewModel : ViewModel() {
    private val _otpSent = MutableStateFlow(false)
    val otpSent = _otpSent
    private val _verificationId = MutableStateFlow<String?>(null)
    private val _authState = MutableStateFlow<String?>(null)

    //    private val _isSignedInSuccessfully = MutableStateFlow(false)
//    val isSignedInSuccessfully = _isSignedInSuccessfully
    private val _isSignedInSuccessfully = MutableSharedFlow<Boolean>(replay = 1)
    val isSignedInSuccessfully = _isSignedInSuccessfully.asSharedFlow()
    private val _isACurrentUser = MutableStateFlow(false)
    val isACurrentUser = _isACurrentUser

    init {
        Utils.getAuthInstance().currentUser?.let {
            _isACurrentUser.value = true
        }
    }
    fun sendOTP(userNumber: String, activity: Activity) {
        _otpSent.value = false
        _verificationId.value = null
        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                Utils.hideDialog()

            }

            override fun onVerificationFailed(p0: FirebaseException) {
                _otpSent.value = false  // Explicitly show failure in state
                Utils.hideDialog()
                Utils.showToast(activity, "Failed: ${p0.localizedMessage}")
            }

            override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                _verificationId.value = verificationId
                _otpSent.value = true
                Utils.hideDialog()
            }
        }
        val cleanNumber = userNumber.trim()
        val options = PhoneAuthOptions.newBuilder(Utils.getAuthInstance())
            .setPhoneNumber("+91$cleanNumber").setTimeout(15L, TimeUnit.SECONDS)
            .setActivity(activity).setCallbacks(callbacks).build()
        PhoneAuthProvider.verifyPhoneNumber(options)

    }

    fun signInWithPhoneAuthCredential(otp: String, userNumber: String, user: Users) {
        val credential = PhoneAuthProvider.getCredential(_verificationId.value ?: "", otp)
        if (_verificationId.value.isNullOrEmpty()) {
           return
        }
        FirebaseMessaging.getInstance().token.addOnCompleteListener{
            user.userToken = it.result
            Utils.getAuthInstance().signInWithCredential(credential).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // below added for testing

                    //above added for testing
                    val firebaseUser = Utils.getAuthInstance().currentUser
                    val uid = firebaseUser?.uid
                    // Log.d("OTP", "Login success: $uid")
                    if (uid != null) {
                        val userWithUid = user.copy(uid = uid)
                        FirebaseDatabase.getInstance()
                            .getReference("AllUsers")
                            .child("Users")
                            .child(uid)
                            .setValue(userWithUid)
                    }
                    _isSignedInSuccessfully.tryEmit(true)
                    //I commented because it is causing issue while doing login FirebaseDatabase.getInstance().getReference("AllUsers").child("Users").child(user.uid!!).setValue(user)
                } else {
                    Log.d("OTP", "Login failed")
                    _isSignedInSuccessfully.tryEmit(false)
                }
            }
        }

    }


//    fun signInWithPhoneAuthCredential(otp: String, userNumber: String) {
//        val credential = PhoneAuthProvider.getCredential(_verificationId.value.toString(), otp)
//        Utils.getAuthInstance().signInWithCredential(credential).addOnCompleteListener { task ->
//            if (task.isSuccessful) {
//                // _isSignedInSuccessfully.value = true
//                _isSignedInSuccessfully.tryEmit(true)
//            } else {
//                //_isSignedInSuccessfully.value = false
//                _isSignedInSuccessfully.tryEmit(false) // or false
//                if (task.exception is FirebaseAuthInvalidCredentialsException) {
//                    Log.e("AuthViewModel", "Invalid OTP")
//                } else {
//                    Log.e("AuthViewModel", "Sign in failed: ${task.exception?.message}")
//                }
//            }
//        }
//    }
}
