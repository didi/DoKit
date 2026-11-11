import { DoKitParams } from './CustomViewRegistry';
import { FragmentCloseHandler, SystemFragmentBuilder } from './FragmentRenderer';

/**
 * 业务全屏页画法表。
 * 打开全屏时传入业务画法，就登记到这里。
 */
export class CustomFragmentRegistry {
  /** 已登记的业务页画法。键是页面名。 */
  private static readonly fragments: Map<string, SystemFragmentBuilder> = new Map();

  /** 按页面名登记。同名后登记的覆盖先登记的。 */
  static register(fragmentName: string, builder: SystemFragmentBuilder): void {
    CustomFragmentRegistry.fragments.set(fragmentName, builder);
  }

  /** 按页面名取画法。没有则返回空。 */
  static getBuilder(fragmentName: string): SystemFragmentBuilder | undefined {
    return CustomFragmentRegistry.fragments.get(fragmentName);
  }

  /** 表里是否已有这个页面名。 */
  static has(fragmentName: string): boolean {
    return CustomFragmentRegistry.fragments.has(fragmentName);
  }
}

/**
 * 按页面名画出业务全屏页。
 * 全屏层在栈顶是业务页时调用。没有对应画法时不画。
 */
@Builder
export function buildCustomFragment(fragmentName: string, params: DoKitParams, onClose: FragmentCloseHandler) {
  CustomFragmentRegistry.getBuilder(fragmentName)?.builder(params, onClose);
}
