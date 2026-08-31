import { DoKitBundle } from './core/FragmentRenderer';
import { SimpleDoKitLauncher } from './core/SimpleDoKitLauncher';

/** 自定义 Fragment 类名，业务在宿主 @Builder 中渲染 */
export class CustomKitPageIds {
  static readonly DEMO_FLOAT: string = 'demo_float';
  static readonly DEMO_FULL: string = 'demo_full';
}

export type CustomKitCloseHandler = () => void;

/** 宿主渲染 FRAGMENT_CUSTOM：fragmentClass + onClose */
export type CustomKitPageRenderer = (fragmentClass: string, onClose: CustomKitCloseHandler) => void;

/**
 * 自定义 Fragment 导航，对标 Android DoKit.launchFullScreen(CustomFragment)
 */
export class CustomKitNavigator {
  static requestOpenFullScreen(fragmentClass: string, bundle?: DoKitBundle): void {
    SimpleDoKitLauncher.launchFullScreen(fragmentClass, bundle, false);
  }

  static requestOpenFloating(fragmentClass: string, bundle?: DoKitBundle): void {
    // 鸿蒙 MVP：自定义悬浮窗仍由宿主 @BuilderParam 渲染，此处保留 API 对齐
    CustomKitNavigator.floatingFragmentClass = fragmentClass;
    if (CustomKitNavigator.openFloatingHandler !== null) {
      CustomKitNavigator.openFloatingHandler(fragmentClass);
    }
  }

  private static floatingFragmentClass: string = '';
  private static openFloatingHandler: ((fragmentClass: string) => void) | null = null;
  private static closeFloatingHandler: (() => void) | null = null;

  static setOpenFloatingHandler(handler: ((fragmentClass: string) => void) | null): void {
    CustomKitNavigator.openFloatingHandler = handler;
  }

  static setCloseFloatingHandler(handler: (() => void) | null): void {
    CustomKitNavigator.closeFloatingHandler = handler;
  }

  static getFloatingFragmentClass(): string {
    return CustomKitNavigator.floatingFragmentClass;
  }

  static hasFloatingFragment(): boolean {
    return CustomKitNavigator.floatingFragmentClass.length > 0;
  }

  static clearFloatingFragment(): void {
    CustomKitNavigator.floatingFragmentClass = '';
  }

  static closeFloating(): void {
    CustomKitNavigator.clearFloatingFragment();
    if (CustomKitNavigator.closeFloatingHandler !== null) {
      CustomKitNavigator.closeFloatingHandler();
    }
  }

  static closeFullScreen(): void {
    SimpleDoKitLauncher.finishFullScreen();
  }
}
