import { BlockInfo } from '../bean/BlockInfo';
import { BlockMonitorManager } from './BlockMonitorManager';
import { StackSampler } from './StackSampler';

/**
 * 检测卡顿的核心类，对标 Android MonitorCore
 * 鸿蒙使用 setTimeout(0) 探测主线程消息耗时，替代 Looper.setMessageLogging
 */
export class MonitorCore {
  private static readonly BLOCK_THRESHOLD_MILLIS: number = 200;

  private running: boolean = false;
  private probeTimer: number = -1;
  private stackSampler: StackSampler = new StackSampler();

  start(): void {
    if (this.running) {
      return;
    }
    this.running = true;
    this.stackSampler.init();
    this.scheduleProbe();
  }

  stop(): void {
    this.running = false;
    if (this.probeTimer >= 0) {
      clearTimeout(this.probeTimer);
      this.probeTimer = -1;
    }
    this.stackSampler.shutDown();
  }

  private scheduleProbe(): void {
    if (!this.running) {
      return;
    }
    const startTime = Date.now();
    const threadStartTime = startTime;
    this.stackSampler.startDump();
    this.probeTimer = setTimeout(() => {
      const endTime = Date.now();
      const threadEndTime = endTime;
      this.stackSampler.stopDump();
      if (endTime - startTime > MonitorCore.BLOCK_THRESHOLD_MILLIS) {
        const entries = this.stackSampler.getThreadStackEntries(startTime, endTime);
        if (entries.length > 0) {
          const blockInfo = BlockInfo.newInstance()
            .setMainThreadTimeCost(startTime, endTime, threadStartTime, threadEndTime)
            .setThreadStackEntries(entries)
            .flushString();
          BlockMonitorManager.getInstance().notifyBlockEvent(blockInfo);
        }
      }
      this.scheduleProbe();
    }, 0);
  }
}
