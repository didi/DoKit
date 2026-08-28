import { ActiveToolPage } from '../../model/ActiveToolPage';

/**
 * 悬浮窗和全屏启动器，对标 Android SimpleDoKitLauncher
 */
export class SimpleDoKitLauncher {
  private static openToolPageHandler: ((page: ActiveToolPage) => void) | null = null;
  private static closeToolPageHandler: (() => void) | null = null;

  static setOpenToolPageHandler(handler: ((page: ActiveToolPage) => void) | null): void {
    SimpleDoKitLauncher.openToolPageHandler = handler;
  }

  static setCloseToolPageHandler(handler: (() => void) | null): void {
    SimpleDoKitLauncher.closeToolPageHandler = handler;
  }

  /** 打开内置工具全屏页，对标 launchFullScreen(BaseFragment) */
  static launchFullScreen(page: ActiveToolPage): void {
    if (SimpleDoKitLauncher.openToolPageHandler !== null) {
      SimpleDoKitLauncher.openToolPageHandler(page);
    }
  }

  static closeFullScreen(): void {
    if (SimpleDoKitLauncher.closeToolPageHandler !== null) {
      SimpleDoKitLauncher.closeToolPageHandler();
    }
  }
}
