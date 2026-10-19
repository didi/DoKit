import { PerformanceDataManager } from '../PerformanceDataManager';
import { LineData } from '../widget/LineData';
import { IDataSource } from './IDataSource';

export class CpuDataSource implements IDataSource {
  createData(): LineData {
    const rate = PerformanceDataManager.getInstance().getLastCpuRate();
    return LineData.obtain(rate, `${Math.round(rate)}%`);
  }
}
