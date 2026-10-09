import { fileIo as fs } from '@kit.CoreFileKit';
import { util } from '@kit.ArkTS';
import { hilog } from '@kit.PerformanceAnalysisKit';

/**
 * 进程网络流量采样（rx/tx bytes），供流量浮层计算 KB/s
 */
export class NetFlowSampler {
  private static readonly TAG: string = 'NetFlowSampler';
  private static readonly DOMAIN: number = 0xD0F1;
  private static readonly PROC_NET_DEV: string = '/proc/self/net/dev';
  private static readonly PROC_NET_DEV_FALLBACK: string = '/proc/net/dev';

  private lastRxBytes: number = -1;
  private lastTxBytes: number = -1;
  private totalRxBytes: number = 0;
  private totalTxBytes: number = 0;

  /**
   * 采样一次，返回本周期增量 [rxDelta, txDelta]（字节）
   */
  sampleDelta(): number[] {
    try {
      const totals = NetFlowSampler.readRxTxBytes();
      const rx = totals[0];
      const tx = totals[1];
      if (rx < 0 || tx < 0) {
        return [0, 0];
      }
      if (this.lastRxBytes < 0 || this.lastTxBytes < 0) {
        this.lastRxBytes = rx;
        this.lastTxBytes = tx;
        return [0, 0];
      }
      let rxDelta = rx - this.lastRxBytes;
      let txDelta = tx - this.lastTxBytes;
      this.lastRxBytes = rx;
      this.lastTxBytes = tx;
      if (rxDelta < 0) {
        rxDelta = 0;
      }
      if (txDelta < 0) {
        txDelta = 0;
      }
      this.totalRxBytes += rxDelta;
      this.totalTxBytes += txDelta;
      return [rxDelta, txDelta];
    } catch (e) {
      hilog.info(NetFlowSampler.DOMAIN, NetFlowSampler.TAG,
        'sampleDelta failed: %{public}s', `${e}`);
      return [0, 0];
    }
  }

  getTotalRxBytes(): number {
    return this.totalRxBytes;
  }

  getTotalTxBytes(): number {
    return this.totalTxBytes;
  }

  getTotalBytes(): number {
    return this.totalRxBytes + this.totalTxBytes;
  }

  reset(): void {
    this.lastRxBytes = -1;
    this.lastTxBytes = -1;
    this.totalRxBytes = 0;
    this.totalTxBytes = 0;
  }

  private static readRxTxBytes(): number[] {
    let content = NetFlowSampler.readFile(NetFlowSampler.PROC_NET_DEV);
    if (content.length === 0) {
      content = NetFlowSampler.readFile(NetFlowSampler.PROC_NET_DEV_FALLBACK);
    }
    if (content.length === 0) {
      return [-1, -1];
    }
    let rxSum = 0;
    let txSum = 0;
    const lines = content.split('\n');
    for (let i = 0; i < lines.length; i++) {
      const line = lines[i].trim();
      if (line.length === 0 || line.indexOf('|') >= 0 || line.indexOf('face') >= 0) {
        continue;
      }
      const colon = line.indexOf(':');
      if (colon < 0) {
        continue;
      }
      const iface = line.substring(0, colon).trim();
      if (iface === 'lo') {
        continue;
      }
      const rest = line.substring(colon + 1).trim();
      const tokens = NetFlowSampler.splitTokens(rest);
      // receive bytes = [0], transmit bytes = [8]
      if (tokens.length < 9) {
        continue;
      }
      rxSum += NetFlowSampler.parseLong(tokens[0]);
      txSum += NetFlowSampler.parseLong(tokens[8]);
    }
    return [rxSum, txSum];
  }

  private static readFile(path: string): string {
    try {
      const file = fs.openSync(path, fs.OpenMode.READ_ONLY);
      const buffer = new ArrayBuffer(8192);
      const readLen = fs.readSync(file.fd, buffer);
      fs.closeSync(file);
      const decoder = util.TextDecoder.create('utf-8', { ignoreBOM: true });
      return decoder.decodeToString(new Uint8Array(buffer, 0, readLen));
    } catch (_err) {
      return '';
    }
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
