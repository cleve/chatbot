package com.chat.boot.utils

import android.content.res.Resources
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.chat.boot.R
import org.json.JSONArray
import org.json.JSONObject

val Int.dp: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

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

    fun buildChatBubble(ctx: Context, msg: String, interactionType: String?): View {
        val chatImage = ImageView(ctx)
        val textView = TextView(ctx)
        val conversationContainer = LinearLayout(ctx)
        val customLayout = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT)
        conversationContainer.orientation = LinearLayout.HORIZONTAL

        val params = LinearLayout.LayoutParams(
            40.dp,
            40.dp
        )
        params.marginStart = 0.dp
        params.marginEnd = 5.dp
        chatImage.layoutParams = params

        if (interactionType.equals("bot")) {
            chatImage.setImageResource(R.drawable.bot_icon)
            conversationContainer.addView(chatImage)
            customLayout.setMargins(5.dp, 10.dp, 5.dp, 5.dp)
            customLayout.gravity = Gravity.LEFT
            textView.setBackgroundResource(R.drawable.bot_responses)
            textView.setTextColor(Color.parseColor("#243443"))
        } else if (interactionType.equals("user")) {
            customLayout.setMargins(40.dp, 5.dp, 10.dp, 5.dp)
            customLayout.gravity = Gravity.RIGHT
            textView.setBackgroundResource(R.drawable.user_responses)
            textView.setTextColor(Color.parseColor("#FFFFFF"))
        }
        textView.setPadding(10.dp, 10.dp, 10.dp, 10.dp)
        textView.textSize = 14f
        textView.text = msg
        conversationContainer.layoutParams = customLayout
        conversationContainer.addView(textView)
        return conversationContainer

    }

    fun sendPayloadMessage(ctx: Context, msg: String, chatHistory: LinearLayout) {
        Log.i("sending payload", msg)
        val queue = Volley.newRequestQueue(ctx)
        val jsonBody = JSONObject()
        jsonBody.put("sender", "android-app-user-id")
        jsonBody.put("message", msg)
        val rasaRequest: StringRequest = object : StringRequest(
                Method.POST, serverUrl,
                Response.Listener {
                    response ->
                    Log.d("HttpClient", "success! response: $response")
                    val jsonArray = JSONArray(response)
                    if (jsonArray.length() == 0) {
                        chatHistory.addView(buildChatBubble(ctx, "server error", "bot"))
                    } else {
                        // Sadly JSONArray does not expose an iterator, doing it in the old way
                        for (ii in 0 until jsonArray.length()) {
                            val jsonObject = jsonArray.getJSONObject(ii)
                            if (jsonObject.has("text")) {
                                chatHistory.addView(buildChatBubble(ctx, jsonObject["text"].toString(), "bot"))
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

        // Adding user message to the stack
        val userMessage: View = buildChatBubble(ctx, msg, "user")
        chatHistory.addView(userMessage)

        val rasaRequest: StringRequest = object : StringRequest(
            Method.POST, serverUrl,
            Response.Listener {
                response ->
                Log.d("HttpClient", "success! response: $response")
                val jsonArray = JSONArray(response)
                if (jsonArray.length() == 0) {
                    chatHistory.addView(buildChatBubble(ctx, "server error", "bot"))
                } else {
                    // Sadly JSONArray does not expose an iterator, doing it in the old way
                    for (ii in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(ii)
                        if (jsonObject.has("text")) {
                            chatHistory.addView(buildChatBubble(ctx, jsonObject["text"].toString(), "bot"))
                        } else if (jsonObject.has("custom")) {
                            val objectContainer: MutableMap<String, Any?> = UIFactory.elementCreator(ctx, jsonObject)
                            chatHistory.addView(objectContainer["layout"] as View?)
                            val arrayOfButtons: MutableList<MutableMap<String, Any>> = objectContainer["objects"] as MutableList<MutableMap<String, Any>>
                            val customObject: JSONObject = jsonObject["custom"] as JSONObject
                            if (customObject.getString("layout").equals("image_selector")) {
                                arrayOfButtons.forEach {
                                        item ->
                                    run {
                                        val buttonOption: String = item["option"] as String
                                        val payload: String = item["action"] as String
                                        val imageView: ImageView = item["object"] as ImageView
                                        imageView.setOnClickListener {
                                            //sendPayloadMessage(ctx, payload, chatHistory)
                                            Log.d("Sending message to the service => ", payload)
                                        }
                                        imageView.bringToFront()
                                    }
                                }
                            } else {
                                arrayOfButtons.forEach {
                                        item ->
                                    run {
                                        val buttonOption: String = item["option"] as String
                                        val payload: String = item["action"] as String
                                        val button: Button = item["object"] as Button
                                        button.setOnClickListener {
                                            sendPayloadMessage(ctx, payload, chatHistory)
                                        }
                                    }
                                }
                            }
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