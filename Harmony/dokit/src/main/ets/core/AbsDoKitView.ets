/**
 * 自定义悬浮窗基类，对标 Android kit/core/AbsDoKitView。
 * 鸿蒙浮窗用 @Builder 绘制，这个类只保留与 Android 对齐的类型。
 */
export abstract class AbsDoKitView {
  /** 日志标签。 */
  readonly TAG: string = 'AbsDoKitView';
}
