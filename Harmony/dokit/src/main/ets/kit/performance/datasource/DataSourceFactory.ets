import { IDataSource } from './IDataSource';
import { FpsDataSource } from './FpsDataSource';
import { CpuDataSource } from './CpuDataSource';
import { RamDataSource } from './RamDataSource';
import { NetworkDataSource } from './NetworkDataSource';

/**
 * 性能数据源工厂，对标 Android DataSourceFactory
 */
export class DataSourceFactory {
  static readonly TYPE_NETWORK: number = 1;
  static readonly TYPE_CPU: number = 2;
  static readonly TYPE_RAM: number = 3;
  static readonly TYPE_FPS: number = 4;

  static createDataSource(performanceType: number): IDataSource {
    if (performanceType === DataSourceFactory.TYPE_CPU) {
      return new CpuDataSource();
    }
    if (performanceType === DataSourceFactory.TYPE_RAM) {
      return new RamDataSource();
    }
    if (performanceType === DataSourceFactory.TYPE_NETWORK) {
      return new NetworkDataSource();
    }
    return new FpsDataSource();
  }
}
