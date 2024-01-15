package com.phuong.chatudp.data.repository


import com.phuong.chatudp.model.MessageResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.InetSocketAddress

object UdpServer {

    fun receivedMessage(port: Int): Flow<MessageResponse> {
        return flow {
            val socket = DatagramSocket(port)
            try {
                while (true) {
                    val receiveData = ByteArray(1024)
                    val receivePacket = DatagramPacket(receiveData, receiveData.size)
                    socket.receive(receivePacket)
                    val message = String(receivePacket.data, 0, receivePacket.length)
                    emit(MessageResponse.Success(message))
                }
            } catch (e: Exception) {
                emit(MessageResponse.Error(e))
                socket.close()
            }
        }.flowOn(Dispatchers.IO)
    }

    fun sendMessage(serverIp: String, message: String, port: Int): Flow<MessageResponse> {
        return flow {
            val socket = DatagramSocket()
            try {
                val sendData = message.toByteArray()
                val serverAddress = InetSocketAddress(serverIp, port)
                val sendPacket =
                    DatagramPacket(sendData, sendData.size, serverAddress)
                socket.send(sendPacket)
                emit(MessageResponse.SendMessageSuccess())
            } catch (e: Exception) {
                emit(MessageResponse.Error(e))
                e.printStackTrace()
                socket.close()
            }
        }.flowOn(Dispatchers.IO)
    }

}