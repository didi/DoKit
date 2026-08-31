import { BlockInfo } from '../bean/BlockInfo';
import { BlockCanaryUtils } from './BlockCanaryUtils';
import { MonitorCore } from './MonitorCore';
import { OnBlockInfoUpdateListener } from './OnBlockInfoUpdateListener';

/**
 * 卡顿检测管理类，对标 Android BlockMonitorManager
 */
export class BlockMonitorManager {
  private static readonly MAX_SIZE: number = 50;
  private static instance: BlockMonitorManager | null = null;

  private mIsRunning: boolean = false;
  private monitorCore: MonitorCore | null = null;
  private blockInfoList: BlockInfo[] = [];
  private onBlockInfoUpdateListener: OnBlockInfoUpdateListener | null = null;

  static getInstance(): BlockMonitorManager {
    if (BlockMonitorManager.instance === null) {
      BlockMonitorManager.instance = new BlockMonitorManager();
    }
    return BlockMonitorManager.instance;
  }

  start(): void {
    if (this.mIsRunning) {
      return;
    }
    if (this.monitorCore === null) {
      this.monitorCore = new MonitorCore();
    }
    this.monitorCore.start();
    this.mIsRunning = true;
  }

  isRunning(): boolean {
    return this.mIsRunning;
  }

  stop(): void {
    if (!this.mIsRunning) {
      return;
    }
    if (this.monitorCore !== null) {
      this.monitorCore.stop();
      this.monitorCore = null;
    }
    this.mIsRunning = false;
  }

  setOnBlockInfoUpdateListener(listener: OnBlockInfoUpdateListener | null): void {
    this.onBlockInfoUpdateListener = listener;
  }

  notifyBlockEvent(blockInfo: BlockInfo): void {
    blockInfo.concernStackString = BlockCanaryUtils.concernStackString(blockInfo);
    blockInfo.time = Date.now();
    if (blockInfo.concernStackString.length === 0) {
      return;
    }
    if (this.blockInfoList.length >= BlockMonitorManager.MAX_SIZE) {
      this.blockInfoList.shift();
    }
    this.blockInfoList.push(blockInfo);
    if (this.onBlockInfoUpdateListener !== null) {
      this.onBlockInfoUpdateListener(blockInfo);
    }
  }

  getBlockInfoList(): BlockInfo[] {
    return [...this.blockInfoList];
  }
}
