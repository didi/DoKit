/**
 * 性能监控回调，对标 Android DoKitCallBack
 */
export interface DoKitCallBack {
  onFpsCallBack?: (value: number, filePath: string) => void;
  onCpuCallBack?: (value: number, filePath: string) => void;
  onMemoryCallBack?: (value: number, filePath: string) => void;
  onNetworkCallBack?: (record: Record<string, Object>) => void;
}
