import { DoKitParams } from './CustomViewRegistry';
import { FragmentCloseHandler, SystemFragmentBuilder } from './FragmentRenderer';

/**
 * 宿主自定义全屏页注册表（SubWindow 路径不依赖页面 @BuilderParam）
 */
export class CustomFragmentRegistry {
  private static readonly fragments: Map<string, SystemFragmentBuilder> = new Map();

  static register(fragmentName: string, builder: SystemFragmentBuilder): void {
    CustomFragmentRegistry.fragments.set(fragmentName, builder);
  }

  static getBuilder(fragmentName: string): SystemFragmentBuilder | undefined {
    return CustomFragmentRegistry.fragments.get(fragmentName);
  }

  static has(fragmentName: string): boolean {
    return CustomFragmentRegistry.fragments.has(fragmentName);
  }
}

@Builder
export function buildCustomFragment(fragmentName: string, params: DoKitParams, onClose: FragmentCloseHandler) {
  CustomFragmentRegistry.getBuilder(fragmentName)?.builder(params, onClose);
}
