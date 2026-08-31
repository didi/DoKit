import { webview } from '@kit.ArkWeb';
import { DoKitBundle, FragmentCloseHandler } from '../core/FragmentRenderer';
import { BundleKey } from '../../constant/BundleKey';

/** 对标 Android WebDoorDefaultFragment */
export class WebDoorDefaultFragment {
  static readonly CLASS_NAME: string = 'WebDoorDefaultFragment';
}

/**
 * H5任意门默认 WebView 页，对标 Android WebDoorDefaultFragment
 */
@Component
export struct WebDoorWebViewPage {
  url: string = '';
  onClose: () => void = () => {};
  private webController: webview.WebviewController = new webview.WebviewController();

  build() {
    Column() {
      Row() {
        Text('←')
          .fontSize(22)
          .fontColor('#333333')
          .padding({ left: 12, right: 12, top: 8, bottom: 8 })
          .onClick(() => {
            if (this.webController.accessBackward()) {
              this.webController.backward();
              return;
            }
            this.onClose();
          })

        Text('H5任意门')
          .fontSize(18)
          .fontWeight(FontWeight.Medium)
          .fontColor('#111111')
          .layoutWeight(1)
          .textAlign(TextAlign.Center)

        Text('✕')
          .fontSize(20)
          .fontColor('#666666')
          .padding(8)
          .onClick(() => {
            this.onClose();
          })
      }
      .width('100%')
      .height(56)
      .padding({ right: 8 })
      .backgroundColor(Color.White)

      Web({ src: this.url, controller: this.webController })
        .width('100%')
        .layoutWeight(1)
        .javaScriptAccess(true)
        .domStorageAccess(true)
        .fileAccess(true)
        .onlineImageAccess(true)
        .imageAccess(true)
    }
    .width('100%')
    .height('100%')
    .backgroundColor(Color.White)
  }
}

@Builder
export function renderWebDoorDefaultFragment(bundle: DoKitBundle, onClose: FragmentCloseHandler) {
  WebDoorWebViewPage({
    url: String(bundle[BundleKey.KEY_URL] ?? ''),
    onClose
  });
}
