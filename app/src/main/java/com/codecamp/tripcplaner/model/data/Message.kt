package com.codecamp.tripcplaner.model.data

data class Message(val content: String, val role: String) {
    val isUser: Boolean
        get() = role == "user"
}