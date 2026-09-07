/** 参数：对标 Android Bundle extras */
export type DoKitParams = Record<string, Object>;

/**
 * 关闭处理handler
 */
export type CloseHandler = Callback<void>;

/**
 * 自定义视图builder
 */
export type CustomViewBuilder = WrappedBuilder<[DoKitParams, CloseHandler]>;


export class CustomViewRegistry {
  private static readonly customViews: Map<string, CustomViewBuilder> = new Map();

  static registerBuilder(fragmentName: string, builder: CustomViewBuilder): void {
    CustomViewRegistry.customViews.set(fragmentName, builder);
  }

  static getBuilder(fragmentName: string): CustomViewBuilder | undefined {
    return CustomViewRegistry.customViews.get(fragmentName);
  }
}

