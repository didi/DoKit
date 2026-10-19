import { PerformanceDataManager } from '../PerformanceDataManager';
import { LineData } from '../widget/LineData';
import { IDataSource } from './IDataSource';

/**
 * 流量数据源，对标 Android NetworkDataSource（周期增量 KB）
 */
export class NetworkDataSource implements IDataSource {
  createData(): LineData {
    const kb = PerformanceDataManager.getInstance().getLastNetworkInfo();
    if (kb <= 0) {
      return LineData.obtain(0, null);
    }
    return LineData.obtain(kb, NetworkDataSource.formatSize(kb * 1024));
  }

  private static formatSize(bytes: number): string {
    if (bytes < 1024) {
      return `${Math.round(bytes)}B`;
    }
    if (bytes < 1024 * 1024) {
      return `${Math.round(bytes / 1024)}KB`;
    }
    return `${(bytes / (1024 * 1024)).toFixed(1)}MB`;
  }
}
