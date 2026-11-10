import { hilog } from '@kit.PerformanceAnalysisKit';
import { SystemFragmentBuilder } from './FragmentRenderer';
import { DoKitViewManager } from './DoKitViewManager';
import { DoKitFloatOptions } from '../DoKitFloatOptions';
import { CustomViewBuilder, CustomViewRegistry, DoKitParams } from './CustomViewRegistry';
import { CustomFragmentRegistry } from './CustomFragmentRegistry';
import { SystemFragmentRegistry } from './SystemFragmentRigistry';

const DOMAIN = 0x0000;
const TAG = 'DoKitH5';

/**
 * 悬浮窗和全屏启动器，对标 Android kit/core/SimpleDoKitLauncher
 *
 * 自定义组件注册流程（）
 */
export class DoKitLauncher {

  /** 按名字登记浮窗画法，再挂上。同名已在时只更新画法和摆放，不改位置。 */
  static launchFloatingView(
    targetName: string,
    builder: CustomViewBuilder,
    params?: DoKitParams,
    options?: DoKitFloatOptions
  ): void {
    CustomViewRegistry.registerBuilder(targetName, builder);
    DoKitViewManager.launchFloatingView(targetName, builder, params, options);
  }

  /** 按名字卸下浮窗，层表跟着去掉这一层。 */
  static removeFloatingView(targetName: string): void {
    DoKitViewManager.removeFloatingView(targetName);
  }


  /** 打开全屏工具根页（清栈）。传入 builder 时按系统/自定义登记画法。 */
  static launchFullScreen(
    targetName: string,
    builder?: SystemFragmentBuilder,
    params?: DoKitParams,
    isSystemFragment: boolean = false
  ): void {
    if (builder !== undefined && isSystemFragment == false) {
      CustomFragmentRegistry.register(targetName, builder);
    }
    DoKitViewManager.launchFullScreen(targetName, params, isSystemFragment);
  }

  /** 全屏内再打开下级页（压栈）。传入 builder 时按系统/自定义登记画法。 */
  static pushFullScreen(
    targetName: string,
    builder?: SystemFragmentBuilder,
    params?: DoKitParams,
    isSystemFragment: boolean = false
  ): void {
    if (builder !== undefined && isSystemFragment == false) {
      CustomFragmentRegistry.register(targetName, builder);
    }
    DoKitViewManager.pushFullScreen(targetName, params, isSystemFragment);
  }


  /** 按名字从栈里清除页面。 */
  static popFullScreenPage(targetName: string): void {
    DoKitViewManager.popFullScreenPage(targetName);
  }

  /** 关闭栈顶页面。栈空则直接返回。 */
  static popFullScreen(): void {
    DoKitViewManager.popFullScreen();
  }

  /** 清掉全部全屏页。 */
  static finishFullScreen(): void {
    DoKitViewManager.finishFullScreen();
  }

}
