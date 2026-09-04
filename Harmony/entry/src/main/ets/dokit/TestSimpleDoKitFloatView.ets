import { CustomKitCloseHandler, DoKit, DoKitBundle } from '@didi-hummer/dokit';
import { CustomDokitFragment } from './CustomDokitFragment';
import { ViewSetupHelper } from './ViewSetupHelper';

/** 对标 Android dokit/TestSimpleDoKitFloatView */
export class TestSimpleDoKitFloatView {
  static readonly CLASS_NAME: string = 'TestSimpleDoKitFloatView';
}

@Builder
export function buildTestSimpleDoKitFloatView(_bundle: DoKitBundle, onClose: CustomKitCloseHandler) {
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
        DoKit.launchFullScreen(CustomDokitFragment.CLASS_NAME, { "test": 'test' });
      }
    })
  }
  .backgroundColor(Color.White)
  .borderRadius(8)
  .shadow({ radius: 8, color: '#33000000', offsetX: 0, offsetY: 2 })
  .position({ x: 48, y: 180 })
}
