import { CustomKitCloseHandler, DoKit, DoKitBundle } from '@didi/dokit';
import { ViewSetupHelper } from './ViewSetupHelper';

/** 对标 Android dokit/CustomDokitFragment */
export class CustomDokitFragment {
  static readonly CLASS_NAME: string = 'CustomDokitFragment';
}

@Builder
export function buildCustomDokitFragment(bundle: DoKitBundle, onClose: CustomKitCloseHandler) {
  Column() {
    Row() {
      Text('我是自定义页面')
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
          onClose();
        })
    }
    .width('100%')
    .height(56)
    .padding({ left: 8, right: 8 })
    .backgroundColor(Color.White)

    Scroll() {
      ViewSetupHelper({
        showFullScreenLauncher: true,
        onLaunchFullScreen: () => {
          DoKit.launchFullScreen(CustomDokitFragment.CLASS_NAME, { "test": 'test' });
        }
      })
    }
    .layoutWeight(1)
    .scrollBar(BarState.Off)
  }
  .width('100%')
  .height('100%')
  .backgroundColor('#F5F6F8')
}
