package com.chat.boot.utils

object Constants{

    const val serverUrl: String = "http://192.168.2.101:5005/webhooks/skynet/webhook"
    const val registerUrl: String = "http://192.168.2.101/register"

    init {
        println("Constants class invoked.")
    }
}