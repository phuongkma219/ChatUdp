package com.phuong.chatudp

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.phuong.chatudp.data.repository.UdpServer
import com.phuong.chatudp.model.MessageResponse
import com.phuong.chatudp.utils.Constants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.net.Inet4Address
import java.net.NetworkInterface
import java.net.SocketException

class MainViewModel : ViewModel() {
    private var _stateMessage = MutableStateFlow<MessageResponse>(MessageResponse.Idle())
    val stateMessage get() = _stateMessage.asStateFlow()
    private var ip = ""
    private var port = 0
    fun setUp(ip : String, isServer : Boolean){
        port = if (isServer){
            Constants.CLIENT_PORT
        } else{
            Constants.SERVER_PORT
        }
        this.ip = ip
    }


    fun startServer(port : Int){
        viewModelScope.launch {
            UdpServer.receivedMessage(port).collect{
                if (it is MessageResponse.Success){
                    Log.d("kkk", "startServer: ${it.message}")
                    if (it.message.equals(CLOSE)){
                        _stateMessage.value = MessageResponse.Close()
                    }
                    else{
                        _stateMessage.value = it
                    }
                }
                else{
                    _stateMessage.value = it
                }
            }
        }
    }

    fun sendMessage(message : String){
        viewModelScope.launch {
            UdpServer.sendMessage(ip,message,port).collect{
                _stateMessage.value = it
            }
        }
    }
    fun closeServer(){
        viewModelScope.launch {
            UdpServer.sendMessage(ip, CLOSE,port).collect{
                _stateMessage.value = it
            }
        }

    }

    fun isCheckNetwork() :Boolean{
        return getLocalIpAddress().isNullOrBlank()
    }

    private fun getLocalIpAddress(): String? {
        try {
            val en = NetworkInterface.getNetworkInterfaces()
            while (en.hasMoreElements()) {
                val intf = en.nextElement()
                val enumIpAddr = intf.inetAddresses
                while (enumIpAddr.hasMoreElements()) {
                    val inetAddress = enumIpAddr.nextElement()
                    if (!inetAddress.isLoopbackAddress && inetAddress is Inet4Address) {
                        return inetAddress.getHostAddress()
                    }
                }
            }
        } catch (ex: SocketException) {
            ex.printStackTrace()
        }
        return null
    }
    companion object{
        private const val CLOSE = "Close"
    }
}