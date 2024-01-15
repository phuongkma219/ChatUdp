package com.phuong.chatudp.model

sealed class MessageResponse {
    class Idle : MessageResponse()
    class Typing : MessageResponse()
    class Close : MessageResponse()
    class SendMessageSuccess : MessageResponse()
    class Success(val message: String) : MessageResponse()
    class Error(val ex: Exception?) : MessageResponse()
}
