/**
 * 默认 WebView 打开入口，由 DoKitFloatingWindow 注册
 */
export class WebDoorNavigator {
  private static openWebViewHandler: ((url: string) => void) | null = null;

  static setOpenWebViewHandler(handler: ((url: string) => void) | null): void {
    WebDoorNavigator.openWebViewHandler = handler;
  }

  static openWebView(url: string): void {
    if (WebDoorNavigator.openWebViewHandler !== null) {
      WebDoorNavigator.openWebViewHandler(url);
    }
  }
}
