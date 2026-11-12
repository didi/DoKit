/**
 * 浮层容器标记，对标 Android DoKitFrameLayout 的静态字段。
 * 用来区分 DoKit 自己的根容器和里面的子容器。
 */
export class DoKitFrameLayoutConstants {
  /** 根容器。 */
  static readonly FLAG_ROOT: number = 100;
  /** 子容器。 */
  static readonly FLAG_CHILD: number = 200;
}
