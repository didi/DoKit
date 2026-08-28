/**
 * 自定义 Kit 页面 ID，业务可扩展自己的 id 并在宿主 @Builder 中渲染
 */
export class CustomKitPageIds {
  static readonly DEMO_FLOAT: string = 'demo_float';
  static readonly DEMO_FULL: string = 'demo_full';
}

/** 自定义 Kit 关闭回调 */
export type CustomKitCloseHandler = () => void;

/** 宿主提供的全屏/悬浮窗渲染 Builder：pageId + onClose */
export type CustomKitPageRenderer = (pageId: string, onClose: CustomKitCloseHandler) => void;

/**
 * 自定义 Kit 导航，对标 Android DoKit.launchFullScreen / launchFloating
 * 渲染由宿主 DoKitFloatingWindow 的 @BuilderParam 完成（ArkTS 限制）
 */
export class CustomKitNavigator {
  private static fullScreenPageId: string = '';
  private static floatingPageId: string = '';
  private static openFullScreenHandler: ((pageId: string) => void) | null = null;
  private static openFloatingHandler: ((pageId: string) => void) | null = null;
  private static closeFullScreenHandler: (() => void) | null = null;
  private static closeFloatingHandler: (() => void) | null = null;

  static setOpenFullScreenHandler(handler: ((pageId: string) => void) | null): void {
    CustomKitNavigator.openFullScreenHandler = handler;
  }

  static setOpenFloatingHandler(handler: ((pageId: string) => void) | null): void {
    CustomKitNavigator.openFloatingHandler = handler;
  }

  static setCloseFullScreenHandler(handler: (() => void) | null): void {
    CustomKitNavigator.closeFullScreenHandler = handler;
  }

  static setCloseFloatingHandler(handler: (() => void) | null): void {
    CustomKitNavigator.closeFloatingHandler = handler;
  }

  static requestOpenFullScreen(pageId: string): void {
    CustomKitNavigator.fullScreenPageId = pageId;
    if (CustomKitNavigator.openFullScreenHandler !== null) {
      CustomKitNavigator.openFullScreenHandler(pageId);
    }
  }

  static requestOpenFloating(pageId: string): void {
    CustomKitNavigator.floatingPageId = pageId;
    if (CustomKitNavigator.openFloatingHandler !== null) {
      CustomKitNavigator.openFloatingHandler(pageId);
    }
  }

  static getFullScreenPageId(): string {
    return CustomKitNavigator.fullScreenPageId;
  }

  static getFloatingPageId(): string {
    return CustomKitNavigator.floatingPageId;
  }

  static hasFullScreenPage(): boolean {
    return CustomKitNavigator.fullScreenPageId.length > 0;
  }

  static hasFloatingPage(): boolean {
    return CustomKitNavigator.floatingPageId.length > 0;
  }

  static clearFullScreenPage(): void {
    CustomKitNavigator.fullScreenPageId = '';
  }

  static clearFloatingPage(): void {
    CustomKitNavigator.floatingPageId = '';
  }

  static closeFullScreen(): void {
    CustomKitNavigator.clearFullScreenPage();
    if (CustomKitNavigator.closeFullScreenHandler !== null) {
      CustomKitNavigator.closeFullScreenHandler();
    }
  }

  static closeFloating(): void {
    CustomKitNavigator.clearFloatingPage();
    if (CustomKitNavigator.closeFloatingHandler !== null) {
      CustomKitNavigator.closeFloatingHandler();
    }
  }
}
