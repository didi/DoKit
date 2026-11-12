/**
 * 全屏页基类标记，对标 Android kit/core/BaseFragment。
 * 鸿蒙全屏页用 @Builder 绘制，这个类只保留与 Android 对齐的类型。
 */
export abstract class BaseFragment {
  /** 日志标签。 */
  readonly TAG: string = 'BaseFragment';
}
