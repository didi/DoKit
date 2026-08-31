import { CustomKitCloseHandler, DoKit, DoKitBundle } from '@didi/dokit';

/** 对标 Android dokit/DemoDoKitView */
export class DemoDoKitView {
  static readonly CLASS_NAME: string = 'DemoDoKitView';
}

/**
 * 对标 Android DemoDoKitView + dk_demo.xml
 */
@Builder
export function buildDemoDoKitView(_bundle: DoKitBundle, onClose: CustomKitCloseHandler) {
  Stack({ alignContent: Alignment.TopEnd }) {
    Column() {
      Text('我是demo浮标')
        .fontSize(14)
        .fontColor('#333333')
    }
    .width(280)
    .height(72)
    .justifyContent(FlexAlign.Center)
    .backgroundColor('#F0F6FF')
    .borderRadius(8)
    .border({ width: 1, color: '#D0E3FF' })

    Text('X')
      .fontSize(20)
      .fontColor('#666666')
      .margin({ top: 8, right: 8 })
      .onClick(() => {
        DoKit.removeFloating(DemoDoKitView.CLASS_NAME);
      })
  }
  .position({ x: 200, y: 200 })
}
