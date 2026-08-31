import { BlockInfo } from '../bean/BlockInfo';

class StackEntry {
  time: number = 0;
  stack: string = '';

  constructor(time: number, stack: string) {
    this.time = time;
    this.stack = stack;
  }
}

/**
 * 堆栈信息采集，对标 Android StackSampler
 */
export class StackSampler {
  private static readonly DEFAULT_SAMPLE_INTERVAL: number = 300;
  private static readonly DEFAULT_MAX_ENTRY_COUNT: number = 100;
  private static readonly SEPARATOR: string = '\r\n';

  private running: boolean = false;
  private sampleTimer: number = -1;
  private stackEntries: StackEntry[] = [];
  private filterCache: string = '';

  init(): void {
    this.stackEntries = [];
  }

  startDump(): void {
    if (this.running) {
      return;
    }
    this.running = true;
    this.dumpInfo();
    this.scheduleNextSample();
  }

  stopDump(): void {
    if (!this.running) {
      return;
    }
    this.running = false;
    this.filterCache = '';
    if (this.sampleTimer >= 0) {
      clearTimeout(this.sampleTimer);
      this.sampleTimer = -1;
    }
  }

  getThreadStackEntries(startTime: number, endTime: number): string[] {
    const result: string[] = [];
    for (let i = 0; i < this.stackEntries.length; i++) {
      const entry = this.stackEntries[i];
      if (startTime < entry.time && entry.time < endTime) {
        result.push(`${BlockInfo.formatTime(entry.time)}${StackSampler.SEPARATOR}${StackSampler.SEPARATOR}${entry.stack}`);
      }
    }
    if (result.length === 0) {
      const stack = new Error('block stack').stack ?? '';
      if (stack.length > 0) {
        result.push(`${BlockInfo.formatTime(endTime)}${StackSampler.SEPARATOR}${StackSampler.SEPARATOR}${stack}`);
      }
    }
    return result;
  }

  shutDown(): void {
    this.stopDump();
    this.stackEntries = [];
  }

  private scheduleNextSample(): void {
    if (!this.running) {
      return;
    }
    this.sampleTimer = setTimeout(() => {
      this.dumpInfo();
      this.scheduleNextSample();
    }, StackSampler.DEFAULT_SAMPLE_INTERVAL);
  }

  private dumpInfo(): void {
    const stack = new Error('block sample').stack ?? '';
    if (stack.length === 0 || this.shouldIgnore(stack)) {
      return;
    }
    if (this.stackEntries.length >= StackSampler.DEFAULT_MAX_ENTRY_COUNT) {
      this.stackEntries.shift();
    }
    this.stackEntries.push(new StackEntry(Date.now(), stack));
  }

  private shouldIgnore(stack: string): boolean {
    if (this.filterCache === stack) {
      return true;
    }
    this.filterCache = stack;
    return false;
  }
}
