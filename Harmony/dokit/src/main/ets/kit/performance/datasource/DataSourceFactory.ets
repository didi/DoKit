/**
 * 性能数据源类型，对标 Android DataSourceFactory
 */
export class DataSourceFactory {
  static readonly TYPE_NETWORK: number = 1;
  static readonly TYPE_CPU: number = 2;
  static readonly TYPE_RAM: number = 3;
  static readonly TYPE_FPS: number = 4;
}
