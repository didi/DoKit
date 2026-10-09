import { hidebug } from '@kit.PerformanceAnalysisKit';
import { hilog } from '@kit.PerformanceAnalysisKit';
import { fileIo as fs } from '@kit.CoreFileKit';
import { util } from '@kit.ArkTS';

/**
 * CPU 采样
 * 优先 hidebug.getCpuUsage()（鸿蒙推荐）
 * /proc/stat 路径在正式机上对应用常 Permission denied，会导致一直 0%
 */
export class CpuSampler {
  private static readonly TAG: string = 'CpuSampler';
  private static readonly DOMAIN: number = 0xD0F1;
  private lastCpuTime: number = -1;
  private lastAppCpuTime: number = -1;
  private hidebugUnavailableLogged: boolean = false;

  sample(): number {
    try {
      // hidebug 返回 0~1，例如 0.5 = 50%
      const usage = hidebug.getCpuUsage();
      if (usage >= 0) {
        const rate = usage * 100;
        return rate > 100 ? 100 : rate;
      }
    } catch (e) {
      if (!this.hidebugUnavailableLogged) {
        this.hidebugUnavailableLogged = true;
        hilog.info(CpuSampler.DOMAIN, CpuSampler.TAG,
          'hidebug.getCpuUsage failed, fallback /proc: %{public}s', `${e}`);
      }
    }
    return this.sampleFromProc();
  }

  reset(): void {
    this.lastCpuTime = -1;
    this.lastAppCpuTime = -1;
  }

  /**
   * 对标 Android getCPUData()（/proc/stat + /proc/self/stat）
   * 多数正式机应用无权限读 /proc/stat，仅作兜底
   */
  private sampleFromProc(): number {
    try {
      const procStatLine = CpuSampler.readFirstLine('/proc/stat');
      const appStatLine = CpuSampler.readFirstLine('/proc/self/stat');
      if (procStatLine.length === 0 || appStatLine.length === 0) {
        return 0;
      }
      const procStats = CpuSampler.splitTokens(procStatLine);
      const appStats = CpuSampler.splitTokens(appStatLine);
      // /proc/stat: cpu user nice system idle iowait irq softirq ...
      // 对齐 Android：从 nice(index2) 累加到 softirq(index8)
      if (procStats.length < 9 || appStats.length < 15) {
        return 0;
      }
      const cpuTime = CpuSampler.parseLong(procStats[2]) + CpuSampler.parseLong(procStats[3]) +
        CpuSampler.parseLong(procStats[4]) + CpuSampler.parseLong(procStats[5]) +
        CpuSampler.parseLong(procStats[6]) + CpuSampler.parseLong(procStats[7]) +
        CpuSampler.parseLong(procStats[8]);
      // utime(13) + stime(14)
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

  private static readFirstLine(path: string): string {
    const file = fs.openSync(path, fs.OpenMode.READ_ONLY);
    const buffer = new ArrayBuffer(512);
    const readLen = fs.readSync(file.fd, buffer);
    fs.closeSync(file);
    const decoder = util.TextDecoder.create('utf-8', { ignoreBOM: true });
    const content = decoder.decodeToString(new Uint8Array(buffer, 0, readLen));
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
