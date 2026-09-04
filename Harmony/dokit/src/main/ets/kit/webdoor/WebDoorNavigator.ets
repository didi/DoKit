import { BundleKey } from '../../constant/BundleKey';
import { DoKitBundle } from '../../core/FragmentRenderer';
import { WebDoorDefaultFragment } from './WebDoorWebViewPage';
import { DoKitLauncher } from '../../core/DoKitLauncher';

/** H5 任意门 WebView，对标 Android WebDoorDefaultFragment */
export class WebDoorNavigator {
  static openWebView(url: string): void {
    const bundle: DoKitBundle = {};
    bundle[BundleKey.KEY_URL] = url;
    DoKitLauncher.launchFullScreen(WebDoorDefaultFragment.CLASS_NAME, bundle, true);
  }
}
