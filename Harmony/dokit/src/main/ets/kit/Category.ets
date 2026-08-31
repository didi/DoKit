/**
 * Kit 分类，对标 Android kit/Category
 * @deprecated 已弃用，保留是为了兼容以前的 API
 */
export class Category {
  /** 业务模块 */
  static readonly BIZ: number = 0;
  /** 常用工具模块 */
  static readonly TOOLS: number = 1;
  /** 性能监控模块 */
  static readonly PERFORMANCE: number = 2;
  /** 视觉工具模块 */
  static readonly UI: number = 3;
  /** 平台工具模块 */
  static readonly PLATFORM: number = 4;
  /** 关闭 */
  static readonly CLOSE: number = 5;
  /** Dokit 版本号 */
  static readonly VERSION: number = 6;
  /** 浮标模式：系统或者内置 */
  static readonly FLOAT_MODE: number = 7;
  /** weex */
  static readonly WEEX: number = 8;
  /** default */
  static readonly DEFAULT: number = 9;
}
