/**
 * 性能检测内存态开关，对标 Android DokitMemoryConfig
 */
export class DokitMemoryConfig {
  /** AppStorage：设置页开关与浮层关闭跨组件同步 */
  static readonly STORE_FPS: string = 'DoKitH5_PerfFpsOpen';
  static readonly STORE_CPU: string = 'DoKitH5_PerfCpuOpen';
  static readonly STORE_RAM: string = 'DoKitH5_PerfRamOpen';
  static readonly STORE_NETWORK: string = 'DoKitH5_PerfNetworkOpen';

  static FPS_STATUS: boolean = false;
  static CPU_STATUS: boolean = false;
  static RAM_STATUS: boolean = false;
  static NETWORK_STATUS: boolean = false;
  static LOG_STATUS: boolean = false;

  /** 静态配置写入 AppStorage，驱动设置页 Toggle 刷新 */
  static publishSwitchToStorage(): void {
    AppStorage.setOrCreate(DokitMemoryConfig.STORE_FPS, DokitMemoryConfig.FPS_STATUS);
    AppStorage.setOrCreate(DokitMemoryConfig.STORE_CPU, DokitMemoryConfig.CPU_STATUS);
    AppStorage.setOrCreate(DokitMemoryConfig.STORE_RAM, DokitMemoryConfig.RAM_STATUS);
    AppStorage.setOrCreate(DokitMemoryConfig.STORE_NETWORK, DokitMemoryConfig.NETWORK_STATUS);
  }
}
