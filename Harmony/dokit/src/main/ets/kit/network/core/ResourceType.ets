/**
 * 网络资源类型，对标 Android kit/network/core/ResourceType
 */
export enum ResourceType {
  DOCUMENT = 'Document',
  STYLESHEET = 'Stylesheet',
  IMAGE = 'Image',
  FONT = 'Font',
  SCRIPT = 'Script',
  XHR = 'XHR',
  WEBSOCKET = 'WebSocket',
  OTHER = 'Other'
}

/** 获取 DevTools 协议值，对标 Android ResourceType.getProtocolValue() */
export function getResourceTypeProtocolValue(type: ResourceType): string {
  return type;
}
