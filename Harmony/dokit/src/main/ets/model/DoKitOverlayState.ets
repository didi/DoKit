/**
 * DoKit Overlay 全局状态，供悬浮球组件与 DoKit API 同步
 */
export class DoKitOverlayState {
  static readonly ICON_VISIBLE_KEY: string = 'dokit_icon_visible';
  static readonly PANEL_OPEN_KEY: string = 'dokit_panel_open';

  static init(defaultIconVisible: boolean = true): void {
    AppStorage.SetOrCreate<boolean>(DoKitOverlayState.ICON_VISIBLE_KEY, defaultIconVisible);
    AppStorage.SetOrCreate<boolean>(DoKitOverlayState.PANEL_OPEN_KEY, false);
  }

  static setIconVisible(visible: boolean): void {
    AppStorage.Set<boolean>(DoKitOverlayState.ICON_VISIBLE_KEY, visible);
  }

  static isIconVisible(): boolean {
    return AppStorage.get<boolean>(DoKitOverlayState.ICON_VISIBLE_KEY) ?? true;
  }

  static requestOpenPanel(): void {
    AppStorage.Set<boolean>(DoKitOverlayState.PANEL_OPEN_KEY, true);
  }

  static clearPanelOpenRequest(): void {
    AppStorage.Set<boolean>(DoKitOverlayState.PANEL_OPEN_KEY, false);
  }
}
