package com.didichuxing.doraemonkit.kit.network.okhttp.core

enum class ResourceType(val protocolValue: String) {
    DOCUMENT("Document"), STYLESHEET("Stylesheet"), IMAGE("Image"), FONT("Font"), SCRIPT("Script"), XHR("XHR"), WEBSOCKET("WebSocket"), OTHER("Other");

}