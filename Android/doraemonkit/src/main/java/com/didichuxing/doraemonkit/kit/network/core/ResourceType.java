package com.didichuxing.doraemonkit.kit.network.core;

public enum ResourceType {
    DOCUMENT("Document"),
    STYLESHEET("Stylesheet"),
    IMAGE("Image"),
    FONT("Font"),
    SCRIPT("Script"),
    XHR("XHR"),
    WEBSOCKET("WebSocket"),
    OTHER("Other");

    private final String mProtocolValue;

    private ResourceType(String protocolValue) {
      mProtocolValue = protocolValue;
    }

    public String getProtocolValue() {
      return mProtocolValue;
    }
  }