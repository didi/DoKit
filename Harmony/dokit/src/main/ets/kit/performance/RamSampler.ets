import { hidebug } from '@kit.PerformanceAnalysisKit';
import { fileIo as fs } from '@kit.CoreFileKit';
import { util } from '@kit.ArkTS';

/**
 * 内存采样，对标 Android PerformanceDataManager.getMemoryData()
 */
export class RamSampler {
  sample(): number {
    try {
      const memInfo = hidebug.getAppNativeMemInfo();
      const pssKb = Number(memInfo.pss);
      if (pssKb > 0) {
        return pssKb / 1024.0;
      }
    } catch (_err) {
      // hidebug 不可用时走 /proc 兜底
    }
    return RamSampler.readVmRssMb();
  }

  private static readVmRssMb(): number {
    try {
      const statusContent = RamSampler.readFileContent('/proc/self/status', 2048);
      const lines = statusContent.split('\n');
      for (let i = 0; i < lines.length; i++) {
        const line = lines[i].trim();
        if (line.startsWith('VmRSS:')) {
          const parts = RamSampler.splitTokens(line);
          if (parts.length >= 2) {
            const kb = parseInt(parts[1], 10);
            if (!Number.isNaN(kb)) {
              return kb / 1024.0;
            }
          }
        }
      }
    } catch (_err) {
      return 0;
    }
    return 0;
  }

  private static readFileContent(path: string, size: number): string {
    const file = fs.openSync(path, fs.OpenMode.READ_ONLY);
    const buffer = new ArrayBuffer(size);
    fs.readSync(file.fd, buffer);
    fs.closeSync(file);
    const decoder = util.TextDecoder.create('utf-8', { ignoreBOM: true });
    return decoder.decodeToString(new Uint8Array(buffer));
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
}
