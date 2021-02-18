package com.chat.boot.utils

object Constants{

    const val serverUrl: String = "http://192.168.1.82:5005/webhooks/skynet/webhook"
    const val registerUrl: String = "http://192.168.1.82/register"

    init {
        println("Constants class invoked.")
    }
}