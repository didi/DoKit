import { DokitMemoryConfig } from '../../config/DokitMemoryConfig';
import { NetFlowSampler } from '../performance/NetFlowSampler';

/**
 * 网络监控开关与流量累计，对标 Android NetworkManager（MVP：流量速率，无抓包列表）
 */
export class NetworkManager {
  private static instance: NetworkManager | null = null;
  private active: boolean = false;
  private startTime: number = 0;
  private readonly sampler: NetFlowSampler = new NetFlowSampler();

  static get(): NetworkManager {
    if (NetworkManager.instance === null) {
      NetworkManager.instance = new NetworkManager();
    }
    return NetworkManager.instance;
  }

  static isActive(): boolean {
    return NetworkManager.get().active;
  }

  startMonitor(): void {
    if (this.active) {
      return;
    }
    this.active = true;
    this.startTime = Date.now();
    this.sampler.reset();
    DokitMemoryConfig.NETWORK_STATUS = true;
  }

  stopMonitor(): void {
    if (!this.active) {
      return;
    }
    this.active = false;
    this.startTime = 0;
    DokitMemoryConfig.NETWORK_STATUS = false;
    this.sampler.reset();
  }

  getSampler(): NetFlowSampler {
    return this.sampler;
  }

  getRunningTime(): number {
    if (this.startTime === 0) {
      return 0;
    }
    return Date.now() - this.startTime;
  }

  /** 上行累计（发送），对齐 getTotalRequestSize */
  getTotalRequestSize(): number {
    return this.sampler.getTotalTxBytes();
  }

  /** 下行累计（接收），对齐 getTotalResponseSize */
  getTotalResponseSize(): number {
    return this.sampler.getTotalRxBytes();
  }

  getTotalSize(): number {
    return this.sampler.getTotalBytes();
  }
}
