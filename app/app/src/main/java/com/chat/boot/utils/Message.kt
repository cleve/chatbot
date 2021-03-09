package com.chat.boot.utils

class Message(sender: String){
    private val sender = sender
    private lateinit var messageSent: String
    private lateinit var messageReceived: String
    private var messagesReceived = arrayListOf<String>()

    fun sendMessage(msg: String): ArrayList<String> {
        return messagesReceived
    }
}