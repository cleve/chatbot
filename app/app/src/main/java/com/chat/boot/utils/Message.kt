package com.chat.boot.utils

import android.content.Context
import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject

class Message(sender: String){
    private val serverUrl: String = Constants.serverUrl
    private val sender = sender
    private lateinit var messageSent: String
    private lateinit var messageReceived: String
    private var messagesReceived = arrayListOf<String>()

    private fun processMessage(ctx: Context, msg: String, fileEncoded: String?) {
        val queue = Volley.newRequestQueue(ctx)
        val jsonBody = JSONObject()
        jsonBody.put("sender", this.sender)
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
                        Log.e("RasaRequest", "No response from the server")
                    } else {
                        // Sadly JSONArray does not expose an iterator, doing it in the old way
                        for (ii in 0 until jsonArray.length()) {
                            val jsonObject = jsonArray.getJSONObject(ii)
                            if (jsonObject.has("text")) {
                                messageReceived = jsonObject["text"].toString()
                                messagesReceived.add(messageReceived)
                                Log.i("msg", messageReceived)
                            }
                        }
                    }},
                Response.ErrorListener { error -> Log.d("HttpClient", "error: $error") }) {

            override fun getBody(): ByteArray? {
                return jsonBody.toString().toByteArray()
            }
        }
        queue.add(rasaRequest)
    }

    fun sendMessage(ctx: Context, msg: String) {
        processMessage(ctx, msg, null)
    }
}