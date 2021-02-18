package com.chat.boot.utils

import android.content.Context
import android.util.Log
import android.widget.TextView
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject

import com.chat.boot.utils.Constants


class RequestUtils {
    private val serverUrl: String = Constants.serverUrl
    private val registerUrl: String = Constants.registerUrl

    fun registerUser(ctx: Context, user: String, pass: String) {
        val queue = Volley.newRequestQueue(ctx)
        val jsonBody = JSONObject()
        jsonBody.put("user", user)
        jsonBody.put("pwd", pass)

        Log.d("Registering", jsonBody.toString())

    }

    fun validateUser(ctx: Context, user: String, pass: String) {
        val queue = Volley.newRequestQueue(ctx)
        val jsonBody = JSONObject()
        jsonBody.put("user", user)
        jsonBody.put("pwd", pass)

        Log.d("Validating", jsonBody.toString())

    }

    fun sendMessage(ctx: Context, msg: String, chatHistory: TextView, fileEncoded: String?) {
        val queue = Volley.newRequestQueue(ctx)
        val jsonBody = JSONObject()
        jsonBody.put("sender", "android-app-user-id")
        jsonBody.put("message", msg)
        if (fileEncoded != null) {
            val jsonFileHolder = JSONObject()
            val jsonFileObject = JSONObject()
            jsonFileObject.put("type", "png")
            jsonFileObject.put("encoded", fileEncoded)
            jsonFileHolder.put("file", jsonFileObject)
            jsonBody.put("metadata", jsonFileHolder)
        }

        val rasaRequest: StringRequest = object : StringRequest(
            Method.POST, serverUrl,
            Response.Listener {
                response -> Log.d("HttpClient", "success! response: $response")

                Log.d("object response", response)
                val jsonArray = JSONArray(response)

                if (jsonArray.length() == 0) {
                    chatHistory.text = "Server error :("
                } else {
                    val jsonObject = jsonArray.getJSONObject(0)
                    // Response
                    (jsonObject["text"] as String).also { chatHistory.text = it }
                    Log.d("Text", jsonObject["text"].toString()) }
                },
            Response.ErrorListener { error -> Log.d("HttpClient", "error: $error") }) {

            override fun getBody(): ByteArray? {
                return jsonBody.toString().toByteArray()
            }
        }
        queue.add(rasaRequest)
    }
}