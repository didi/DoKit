import { BundleKey } from '../../constant/BundleKey';
import { DoKit } from '../../DoKit';
import { DoKitParams } from '../../core/CustomViewRegistry';
import { renderWebDoorDefaultFragment, WebDoorDefaultFragment } from './WebDoorWebViewPage';

/** H5 任意门 WebView，对标 Android WebDoorDefaultFragment */
export class WebDoorNavigator {
  static openWebView(url: string): void {
    const params: DoKitParams = {};
    params[BundleKey.KEY_URL] = url;
    DoKit.launchFullScreen(
      WebDoorDefaultFragment.CLASS_NAME,
      wrapBuilder(renderWebDoorDefaultFragment),
      params,
      true
    );
  }
}
