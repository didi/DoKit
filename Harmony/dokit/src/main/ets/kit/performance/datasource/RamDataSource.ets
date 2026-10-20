import { PerformanceDataManager } from '../PerformanceDataManager';
import { LineData } from '../widget/LineData';
import { IDataSource } from './IDataSource';

/**
 * 内存数据源：value 用实际 MB，便于 Y 轴按折线峰值自适应
 */
export class RamDataSource implements IDataSource {
  createData(): LineData {
    const info = PerformanceDataManager.getInstance().getLastMemoryInfo();
    return LineData.obtain(info, `${Math.round(info)}MB`);
  }
}
