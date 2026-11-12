/**
 * 性能监控回调，对标 Android DoKitCallBack。
 * 通过 DoKit.Builder.callBack 登记。对应监测开着时，每次采样调用一次。方法都可省略。
 */
export interface DoKitCallBack {
  /** 帧率。value 是最近 1 秒的帧数，最高 120。filePath 是缓存目录下的 doraemon/fps.txt。 */
  onFpsCallBack?: (value: number, filePath: string) => void;
  /** CPU。value 是占用百分比，范围 0 到 100。filePath 是缓存目录下的 doraemon/cpu.txt。 */
  onCpuCallBack?: (value: number, filePath: string) => void;
  /** 内存。value 是应用 PSS，单位 MB。filePath 是缓存目录下的 doraemon/memory.txt。 */
  onMemoryCallBack?: (value: number, filePath: string) => void;
  /**
   * 网络。record 包含：
   * upBytes、downBytes 为本周期上下行字节数；
   * rateKBps 为本周期总流量，单位 KB；
   * totalRequestSize、totalResponseSize、totalSize 为累计请求、响应和总字节数；
   * filePath 是缓存目录下的 doraemon/network.txt。
   */
  onNetworkCallBack?: (record: Record<string, Object>) => void;
}
