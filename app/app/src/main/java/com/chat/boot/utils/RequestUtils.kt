package com.chat.boot.utils

import android.content.Context
import android.util.Log
import android.widget.LinearLayout
import android.widget.TextView
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.chat.boot.R
import org.json.JSONArray
import org.json.JSONObject


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

    fun buildChatBubble(ctx: Context, msg: String, interactionType: String?): TextView {
        val textView = TextView(ctx)
        val customLayout = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT)
        customLayout.setMargins(20,20,20,20)
        textView.setPadding(20, 20, 20, 20)
        textView.layoutParams = customLayout
        textView.textSize = 14f
        textView.text = msg

        if (interactionType == "bot") {
            textView.setBackgroundResource(R.drawable.bot_responses)
""        } else {
            textView.setBackgroundResource(R.drawable.bot_responses)
        }

        return textView

    }

    fun sendMessage(ctx: Context, msg: String, chatHistory: LinearLayout, fileEncoded: String?) {
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
                    chatHistory.addView(buildChatBubble(ctx, "server error", "boot"))
                } else {
                    // Sadly JSONArray does not expose an iterator, doing it in the old way
                    for (ii in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(ii)
                        if (jsonObject.has("text")) {
                            chatHistory.addView(buildChatBubble(ctx, jsonObject["text"].toString(), "boot"))
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
}