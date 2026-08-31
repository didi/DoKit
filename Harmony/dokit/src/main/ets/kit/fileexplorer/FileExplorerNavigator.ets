/**
 * 沙盒浏览导航，对标 Android BundleKey.DIR_KEY 传参
 */
export class FileExplorerNavigator {
  private static initialPath: string = '';

  static setInitialPath(path: string): void {
    FileExplorerNavigator.initialPath = path;
  }

  static consumeInitialPath(): string {
    const path = FileExplorerNavigator.initialPath;
    FileExplorerNavigator.initialPath = '';
    return path;
  }
}
