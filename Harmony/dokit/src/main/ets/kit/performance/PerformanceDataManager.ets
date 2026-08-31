import { common } from '@kit.AbilityKit';
import { DokitMemoryConfig } from '../../config/DokitMemoryConfig';
import { DoKitManager } from '../core/DoKitManager';
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
  private maxFrameRate: number = PerformanceDataManager.MAX_FRAME_RATE;
  private lastFrameRate: number = 60;
  private lastCpuRate: number = 0;
  private lastMemoryRate: number = 0;
  private totalFramesPerSecond: number = 0;
  private frameMonitorRunning: boolean = false;
  private cpuMonitorRunning: boolean = false;
  private memoryMonitorRunning: boolean = false;
  private fpsTimer: number = -1;
  private cpuTimer: number = -1;
  private memoryTimer: number = -1;
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

  getFpsFilePath(): string {
    return this.fpsFilePath;
  }

  getCpuFilePath(): string {
    return this.cpuFilePath;
  }

  getMemoryFilePath(): string {
    return this.memoryFilePath;
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
}
