import { common } from '@kit.AbilityKit';
import { DokitMemoryConfig } from '../../config/DokitMemoryConfig';
import { DoKitManager } from '../../core/DoKitManager';
import { NetworkManager } from '../network/NetworkManager';
import { CpuSampler } from './CpuSampler';
import { RamSampler } from './RamSampler';

/**
 * 性能数据采集，对标 Android PerformanceDataManager
 */
export class PerformanceDataManager {
  private static readonly MAX_FRAME_RATE: number = 120;
  private static readonly FPS_SAMPLING_TIME: number = 1000;
  private static readonly NORMAL_SAMPLING_TIME: number = 500;
  private static instance: PerformanceDataManager | null = null;

  private context: common.Context | null = null;
  private fpsFilePath: string = '';
  private cpuFilePath: string = '';
  private memoryFilePath: string = '';
  private networkFilePath: string = '';
  private maxFrameRate: number = PerformanceDataManager.MAX_FRAME_RATE;
  private lastFrameRate: number = 60;
  private lastCpuRate: number = 0;
  private lastMemoryRate: number = 0;
  private lastNetworkRate: number = 0;
  private lastUpBytes: number = 0;
  private lastDownBytes: number = 0;
  private totalFramesPerSecond: number = 0;
  private frameMonitorRunning: boolean = false;
  private cpuMonitorRunning: boolean = false;
  private memoryMonitorRunning: boolean = false;
  private networkMonitorRunning: boolean = false;
  private fpsTimer: number = -1;
  private cpuTimer: number = -1;
  private memoryTimer: number = -1;
  private networkTimer: number = -1;
  private cpuSampler: CpuSampler = new CpuSampler();
  private ramSampler: RamSampler = new RamSampler();

  static getInstance(): PerformanceDataManager {
    if (PerformanceDataManager.instance === null) {
      PerformanceDataManager.instance = new PerformanceDataManager();
    }
    return PerformanceDataManager.instance;
  }

  init(context: common.Context): void {
    this.context = context.getApplicationContext();
    this.fpsFilePath = `${this.context.cacheDir}/doraemon/fps.txt`;
    this.cpuFilePath = `${this.context.cacheDir}/doraemon/cpu.txt`;
    this.memoryFilePath = `${this.context.cacheDir}/doraemon/memory.txt`;
    this.networkFilePath = `${this.context.cacheDir}/doraemon/network.txt`;
  }

  isMonitoringFps(): boolean {
    return this.frameMonitorRunning;
  }

  isMonitoringCpu(): boolean {
    return this.cpuMonitorRunning;
  }

  isMonitoringMemory(): boolean {
    return this.memoryMonitorRunning;
  }

  isMonitoringNetwork(): boolean {
    return this.networkMonitorRunning;
  }

  startMonitorFrameInfo(): void {
    DokitMemoryConfig.FPS_STATUS = true;
    this.frameMonitorRunning = true;
    this.totalFramesPerSecond = 0;
    this.scheduleFpsTick();
  }

  stopMonitorFrameInfo(): void {
    DokitMemoryConfig.FPS_STATUS = false;
    this.frameMonitorRunning = false;
    if (this.fpsTimer >= 0) {
      clearTimeout(this.fpsTimer);
      this.fpsTimer = -1;
    }
  }

  startMonitorCPUInfo(): void {
    DokitMemoryConfig.CPU_STATUS = true;
    this.cpuMonitorRunning = true;
    this.cpuSampler.reset();
    this.scheduleCpuTick();
  }

  stopMonitorCPUInfo(): void {
    DokitMemoryConfig.CPU_STATUS = false;
    this.cpuMonitorRunning = false;
    if (this.cpuTimer >= 0) {
      clearTimeout(this.cpuTimer);
      this.cpuTimer = -1;
    }
    this.cpuSampler.reset();
  }

  startMonitorMemoryInfo(): void {
    DokitMemoryConfig.RAM_STATUS = true;
    this.memoryMonitorRunning = true;
    this.scheduleMemoryTick();
  }

  stopMonitorMemoryInfo(): void {
    DokitMemoryConfig.RAM_STATUS = false;
    this.memoryMonitorRunning = false;
    if (this.memoryTimer >= 0) {
      clearTimeout(this.memoryTimer);
      this.memoryTimer = -1;
    }
  }

  startMonitorNetFlowInfo(): void {
    DokitMemoryConfig.NETWORK_STATUS = true;
    this.networkMonitorRunning = true;
    this.lastNetworkRate = 0;
    this.lastUpBytes = 0;
    this.lastDownBytes = 0;
    this.scheduleNetworkTick();
  }

  stopMonitorNetFlowInfo(): void {
    DokitMemoryConfig.NETWORK_STATUS = false;
    this.networkMonitorRunning = false;
    if (this.networkTimer >= 0) {
      clearTimeout(this.networkTimer);
      this.networkTimer = -1;
    }
  }

  recordFrame(): void {
    if (this.frameMonitorRunning) {
      this.totalFramesPerSecond++;
    }
  }

  getLastFrameRate(): number {
    return this.lastFrameRate;
  }

  getLastCpuRate(): number {
    return this.lastCpuRate;
  }

  getLastMemoryInfo(): number {
    return this.lastMemoryRate;
  }

  /** 本采样周期总流量速率（KB/s），对齐 NetworkDataSource */
  getLastNetworkInfo(): number {
    return this.lastNetworkRate;
  }

  getLastUpBytes(): number {
    return this.lastUpBytes;
  }

  getLastDownBytes(): number {
    return this.lastDownBytes;
  }

  getFpsFilePath(): string {
    return this.fpsFilePath;
  }

  getCpuFilePath(): string {
    return this.cpuFilePath;
  }

  getMemoryFilePath(): string {
    return this.memoryFilePath;
  }

  getNetworkFilePath(): string {
    return this.networkFilePath;
  }

  getMaxFrameRate(): number {
    return this.maxFrameRate;
  }

  private scheduleFpsTick(): void {
    if (!this.frameMonitorRunning) {
      return;
    }
    this.fpsTimer = setTimeout(() => {
      this.lastFrameRate = this.totalFramesPerSecond;
      if (this.lastFrameRate > this.maxFrameRate) {
        this.lastFrameRate = this.maxFrameRate;
      }
      this.writeFpsDataIntoFile();
      this.totalFramesPerSecond = 0;
      this.scheduleFpsTick();
    }, PerformanceDataManager.FPS_SAMPLING_TIME);
  }

  private scheduleCpuTick(): void {
    if (!this.cpuMonitorRunning) {
      return;
    }
    this.lastCpuRate = this.cpuSampler.sample();
    this.writeCpuDataIntoFile();
    this.cpuTimer = setTimeout(() => {
      this.scheduleCpuTick();
    }, PerformanceDataManager.NORMAL_SAMPLING_TIME);
  }

  private scheduleMemoryTick(): void {
    if (!this.memoryMonitorRunning) {
      return;
    }
    this.lastMemoryRate = this.ramSampler.sample();
    this.writeMemoryDataIntoFile();
    this.memoryTimer = setTimeout(() => {
      this.scheduleMemoryTick();
    }, PerformanceDataManager.NORMAL_SAMPLING_TIME);
  }

  private writeFpsDataIntoFile(): void {
    const callback = DoKitManager.getCallBack();
    if (callback !== null && callback.onFpsCallBack !== undefined) {
      callback.onFpsCallBack(this.lastFrameRate, this.fpsFilePath);
    }
  }

  private writeCpuDataIntoFile(): void {
    const callback = DoKitManager.getCallBack();
    if (callback !== null && callback.onCpuCallBack !== undefined) {
      callback.onCpuCallBack(this.lastCpuRate, this.cpuFilePath);
    }
  }

  private writeMemoryDataIntoFile(): void {
    const callback = DoKitManager.getCallBack();
    if (callback !== null && callback.onMemoryCallBack !== undefined) {
      callback.onMemoryCallBack(this.lastMemoryRate, this.memoryFilePath);
    }
  }

  private scheduleNetworkTick(): void {
    if (!this.networkMonitorRunning) {
      return;
    }
    const delta = NetworkManager.get().getSampler().sampleDelta();
    const rxDelta = delta[0];
    const txDelta = delta[1];
    this.lastDownBytes = rxDelta;
    this.lastUpBytes = txDelta;
    const totalDelta = rxDelta + txDelta;
    // 对齐 Android NetworkDataSource：周期增量转 KB
    this.lastNetworkRate = Math.ceil(totalDelta / 1024);
    this.writeNetworkDataIntoFile();
    this.networkTimer = setTimeout(() => {
      this.scheduleNetworkTick();
    }, PerformanceDataManager.NORMAL_SAMPLING_TIME);
  }

  private writeNetworkDataIntoFile(): void {
    const callback = DoKitManager.getCallBack();
    if (callback !== null && callback.onNetworkCallBack !== undefined) {
      const record: Record<string, Object> = {};
      record['upBytes'] = this.lastUpBytes;
      record['downBytes'] = this.lastDownBytes;
      record['rateKBps'] = this.lastNetworkRate;
      record['totalRequestSize'] = NetworkManager.get().getTotalRequestSize();
      record['totalResponseSize'] = NetworkManager.get().getTotalResponseSize();
      record['totalSize'] = NetworkManager.get().getTotalSize();
      record['filePath'] = this.networkFilePath;
      callback.onNetworkCallBack(record);
    }
  }
}
