package com.didichuxing.doraemonkit.connect

object DokitConnectManager {



    fun getConnectSerial(): String {
        return ConnectConfig.getConnectSerial()
    }

    fun saveConnectSerial(text: String) {
        ConnectConfig.saveConnectSerial(text)
    }
}
