package com.example.blinkitclone.service

import android.content.Context
import android.util.Log
//import com.google.auth.oauth2.GoogleCredentials
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.*
import javax.net.ssl.*

object FCMService {


    //https://console.firebase.google.com/project/onlineshoppingapp-ec3c6/overview
    private const val TAG = "FCMService"
    private const val FCM_URL = "https://fcm.googleapis.com/v1/projects/YOUR_PROJECT_ID/messages:send"

    fun sendNotification(context: Context, token: String, title: String, body: String) {
        try {
            // Load the credentials JSON
            val stream = context.assets.open("your-service-account-file.json")
           /* val credentials = GoogleCredentials.fromStream(stream)
                .createScoped(listOf("https://www.googleapis.com/auth/firebase.messaging"))

            credentials.refreshIfExpired()
            val accessToken = credentials.accessToken.tokenValue*/
            val accessToken = ""

            val client = OkHttpClient()

            val json = """
                {
                  "message": {
                    "token": "$token",
                    "notification": {
                      "title": "$title",
                      "body": "$body"
                    }
                  }
                }
            """.trimIndent()

            val body = json.toRequestBody("application/json".toMediaTypeOrNull())
            val request = Request.Builder()
                .url(FCM_URL)
                .addHeader("Authorization", "Bearer $accessToken")
                .addHeader("Content-Type", "application/json; UTF-8")
                .post(body)
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e(TAG, "FCM Send Failed", e)
                }

                override fun onResponse(call: Call, response: Response) {
                    Log.d(TAG, "FCM Send Response: ${response.body?.string()}")
                }
            })

        } catch (e: Exception) {
            Log.e(TAG, "FCM Exception: ${e.message}", e)
        }
    }
}
