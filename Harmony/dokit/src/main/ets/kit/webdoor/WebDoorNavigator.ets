import { BundleKey } from '../../constant/BundleKey';
import { DoKitBundle } from '../core/FragmentRenderer';
import { WebDoorDefaultFragment } from './WebDoorWebViewPage';
import { SimpleDoKitLauncher } from '../core/SimpleDoKitLauncher';

/** H5 任意门 WebView，对标 Android WebDoorDefaultFragment */
export class WebDoorNavigator {
  static openWebView(url: string): void {
    const bundle: DoKitBundle = {};
    bundle[BundleKey.KEY_URL] = url;
    SimpleDoKitLauncher.launchFullScreen(WebDoorDefaultFragment.CLASS_NAME, bundle, true);
  }
}
