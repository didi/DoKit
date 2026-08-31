import { CustomKitCloseHandler, DoKit } from '@didi/dokit';
import { DemoKitPageIds } from './DemoKitPageIds';
import { ViewSetupHelper } from './ViewSetupHelper';

/** 对标 Android dokit/TestSimpleDoKitFloatView */
@Builder
export function buildTestSimpleDoKitFloatView(onClose: CustomKitCloseHandler) {
  Column() {
    Row() {
      Text('TestSimpleDoKitFloatView')
        .fontSize(14)
        .fontColor('#333333')
        .layoutWeight(1)

      Text('✕')
        .fontSize(18)
        .fontColor('#666666')
        .padding(8)
        .onClick(() => {
          onClose();
        })
    }
    .width('100%')
    .padding({ left: 8, right: 4 })

    ViewSetupHelper({
      showFullScreenLauncher: true,
      onLaunchFullScreen: () => {
        DoKit.launchFullScreen(DemoKitPageIds.CUSTOM_DOKIT_FRAGMENT, { "test": 'test' });
      }
    })
  }
  .backgroundColor(Color.White)
  .borderRadius(8)
  .shadow({ radius: 8, color: '#33000000', offsetX: 0, offsetY: 2 })
  .position({ x: 48, y: 180 })
}
