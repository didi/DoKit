import { fileIo as fs } from '@kit.CoreFileKit';
import { util } from '@kit.ArkTS';

/**
 * CPU 采样，对标 Android PerformanceDataManager.getCPUData()
 */
export class CpuSampler {
  private lastCpuTime: number = -1;
  private lastAppCpuTime: number = -1;

  sample(): number {
    try {
      const procStatLine = CpuSampler.readFirstLine('/proc/stat');
      const appStatLine = CpuSampler.readFirstLine('/proc/self/stat');
      if (procStatLine.length === 0 || appStatLine.length === 0) {
        return 0;
      }
      const procStats = CpuSampler.splitTokens(procStatLine);
      const appStats = CpuSampler.splitTokens(appStatLine);
      if (procStats.length < 9 || appStats.length < 15) {
        return 0;
      }
      const cpuTime = CpuSampler.parseLong(procStats[2]) + CpuSampler.parseLong(procStats[3]) +
        CpuSampler.parseLong(procStats[4]) + CpuSampler.parseLong(procStats[5]) +
        CpuSampler.parseLong(procStats[6]) + CpuSampler.parseLong(procStats[7]) +
        CpuSampler.parseLong(procStats[8]);
      const appTime = CpuSampler.parseLong(appStats[13]) + CpuSampler.parseLong(appStats[14]);
      if (this.lastCpuTime < 0 || this.lastAppCpuTime < 0) {
        this.lastCpuTime = cpuTime;
        this.lastAppCpuTime = appTime;
        return 0;
      }
      const cpuDelta = cpuTime - this.lastCpuTime;
      const appDelta = appTime - this.lastAppCpuTime;
      this.lastCpuTime = cpuTime;
      this.lastAppCpuTime = appTime;
      if (cpuDelta <= 0) {
        return 0;
      }
      const rate = (appDelta / cpuDelta) * 100;
      if (rate < 0) {
        return 0;
      }
      return rate > 100 ? 100 : rate;
    } catch (_err) {
      return 0;
    }
  }

  reset(): void {
    this.lastCpuTime = -1;
    this.lastAppCpuTime = -1;
  }

  private static readFirstLine(path: string): string {
    const file = fs.openSync(path, fs.OpenMode.READ_ONLY);
    const buffer = new ArrayBuffer(512);
    fs.readSync(file.fd, buffer);
    fs.closeSync(file);
    const decoder = util.TextDecoder.create('utf-8', { ignoreBOM: true });
    const content = decoder.decodeToString(new Uint8Array(buffer));
    const lineEnd = content.indexOf('\n');
    if (lineEnd >= 0) {
      return content.substring(0, lineEnd).trim();
    }
    return content.trim();
  }

  private static splitTokens(line: string): string[] {
    const parts: string[] = [];
    let current = '';
    for (let i = 0; i < line.length; i++) {
      const ch = line.charAt(i);
      if (ch === ' ' || ch === '\t') {
        if (current.length > 0) {
          parts.push(current);
          current = '';
        }
      } else {
        current += ch;
      }
    }
    if (current.length > 0) {
      parts.push(current);
    }
    return parts;
  }

  private static parseLong(value: string): number {
    const parsed = parseInt(value, 10);
    return Number.isNaN(parsed) ? 0 : parsed;
  }
}
