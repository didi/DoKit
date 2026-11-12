/** 打开页面或浮窗时带的参数，对标 Android Bundle extras。 */
export type DoKitParams = Record<string, Object>;

/** 关闭浮窗。框架卸掉浮层时调用。 */
export type CloseHandler = Callback<void>;

/** 业务把关闭函数交给框架保存。 */
export type CloseRegistrar = Callback<CloseHandler>;

/** 自定义浮窗画法。参数是打开时的数据和关闭登记函数。 */
export type CustomViewBuilder = WrappedBuilder<[DoKitParams, CloseRegistrar]>;

/** 自定义浮窗画法表。浮层绘制时按名字取出。 */
export class CustomViewRegistry {
  /** 已登记的浮窗画法。键是浮窗名字。 */
  private static readonly customViews: Map<string, CustomViewBuilder> = new Map();

  /** 按名字登记。同名后登记的覆盖先登记的。 */
  static registerBuilder(fragmentName: string, builder: CustomViewBuilder): void {
    CustomViewRegistry.customViews.set(fragmentName, builder);
  }

  /** 按名字取画法。没有则返回空。 */
  static getBuilder(fragmentName: string): CustomViewBuilder | undefined {
    return CustomViewRegistry.customViews.get(fragmentName);
  }
}

