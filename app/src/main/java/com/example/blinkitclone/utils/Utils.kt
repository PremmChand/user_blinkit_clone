package com.example.blinkitclone.utils

import android.app.AlertDialog
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.blinkitclone.databinding.ProgressDialogBinding
import com.google.firebase.auth.FirebaseAuth
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object Utils {

    private var dialog: AlertDialog? = null

    fun showDialog(context: Context, message: String) {
        val progress = ProgressDialogBinding.inflate(LayoutInflater.from(context))
        progress.loadingText.text = message
        dialog = AlertDialog.Builder(context).setView(progress.root).setCancelable(false).create()
        dialog!!.show()
    }

    fun hideDialog() {
        dialog?.dismiss()
    }

    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private var firebaseAuthInstance : FirebaseAuth? = null

    fun getAuthInstance() : FirebaseAuth{
        if(firebaseAuthInstance == null){
            firebaseAuthInstance = FirebaseAuth.getInstance()
        }
        return  firebaseAuthInstance!!
    }

    fun getCurrentUserId(): String? {
        return FirebaseAuth.getInstance().currentUser?.uid
            ?: throw IllegalStateException("No authenticated user; currentUser is null")
    }

    fun getRandomId(length: Int = 10): String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getCurrentDate():String?{
        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        return currentDate.format(formatter)
    }


}