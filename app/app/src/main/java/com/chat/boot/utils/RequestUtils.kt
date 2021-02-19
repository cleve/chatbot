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
                response ->
                Log.d("HttpClient", "success! response: $response")
                val jsonArray = JSONArray(response)
                if (jsonArray.length() == 0) {
                    chatHistory.text = "Server error :("
                } else {
                    var botResponse = ""
                    // Sadly JSONArray does not expose an iterator, doing it in the old way
                    for (ii in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(ii)
                        if (jsonObject.has("text")) {
                            botResponse += jsonObject["text"]
                        }
                    }
                    (botResponse).also { chatHistory.text = it }
                }},
            Response.ErrorListener { error -> Log.d("HttpClient", "error: $error") }) {

            override fun getBody(): ByteArray? {
                return jsonBody.toString().toByteArray()
            }
        }
        queue.add(rasaRequest)
    }
}