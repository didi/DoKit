import { PerformanceDataManager } from '../PerformanceDataManager';
import { LineData } from '../widget/LineData';
import { IDataSource } from './IDataSource';

export class FpsDataSource implements IDataSource {
  createData(): LineData {
    const rate = PerformanceDataManager.getInstance().getLastFrameRate();
    return LineData.obtain(rate, `${Math.round(rate)}`);
  }
}
